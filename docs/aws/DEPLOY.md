# Deploy CourgiousLi Lawn Care to AWS

This guide walks through:

1. **RDS PostgreSQL** — databases in the cloud  
2. **EC2** — run `customer-service` and `signup-service`  
3. **S3** — host the React website + store lawn photo uploads  
4. **Optional: CloudFront** — HTTPS and CDN in front of S3  

Estimated time: 2–4 hours the first time.

---

## Architecture

```
Browser
   │
   ├─► S3 (+ CloudFront)     lawn-ui static files (React build)
   │
   └─► EC2 :8081 / :8082      Spring Boot microservices
           │
           ├─► RDS PostgreSQL  lawn_customers, lawn_signups
           └─► S3 (uploads)    lawn photos per signup
```

---

## Prerequisites

- AWS account  
- AWS CLI installed and configured: `aws configure`  
- Java 17+, Maven, Node.js on your Mac (for building)  
- Your code on GitHub: https://github.com/EdisonSky/courageous-li-lawn-care  

---

## Part 1 — RDS PostgreSQL

### 1.1 Create the database

1. AWS Console → **RDS** → **Create database**  
2. **Standard create** → Engine: **PostgreSQL 16**  
3. Template: **Free tier** (if available)  
4. DB instance identifier: `lawn-db`  
5. Master username: `lawn_admin`  
6. Master password: *(save in a password manager)*  
7. Instance class: `db.t3.micro` or `db.t4g.micro`  
8. Storage: 20 GB (default is fine)  
9. **Public access: Yes** *(learning only; use private subnet in production)*  
10. VPC security group: **Create new** → name `lawn-rds-sg`  
11. Initial database name: `postgres`  
12. Create database  

### 1.2 Allow your IP to connect

1. EC2 → **Security Groups** → open `lawn-rds-sg`  
2. **Inbound rules** → Add rule:  
   - Type: PostgreSQL  
   - Port: 5432  
   - Source: **My IP**  

Later you will also allow the EC2 security group.

### 1.3 Create app databases

From your Mac (install `psql` via `brew install libpq` if needed):

```bash
export RDS_HOST=your-db.xxxxx.us-east-1.rds.amazonaws.com
export PGPASSWORD=your-master-password

psql -h $RDS_HOST -U lawn_admin -d postgres -c "CREATE DATABASE lawn_customers;"
psql -h $RDS_HOST -U lawn_admin -d postgres -c "CREATE DATABASE lawn_signups;"
```

Save the endpoint — you need it for `DB_URL`.

---

## Part 2 — S3 buckets

### 2.1 Website bucket (React UI)

```bash
export AWS_REGION=us-east-1
export WEBSITE_BUCKET=courageous-li-lawn-website-$(aws sts get-caller-identity --query Account --output text)

aws s3 mb s3://$WEBSITE_BUCKET --region $AWS_REGION

# Allow public read for static website (learning setup)
aws s3 website s3://$WEBSITE_BUCKET --index-document index.html --error-document index.html
```

Or run the helper script:

```bash
./scripts/aws-setup-s3.sh
```

### 2.2 Uploads bucket (lawn photos — private)

```bash
export UPLOAD_BUCKET=courageous-li-lawn-uploads-$(aws sts get-caller-identity --query Account --output text)

aws s3 mb s3://$UPLOAD_BUCKET --region $AWS_REGION
```

Block all public access on the **uploads** bucket (default). The app uploads via IAM credentials on EC2.

---

## Part 3 — EC2 for backend services

### 3.1 Launch instance

1. EC2 → **Launch instance**  
2. Name: `lawn-backend`  
3. AMI: **Amazon Linux 2023** (arm64 works on your M4 Mac builds — use **aarch64** AMI on EC2 t4g)  
4. Instance type: `t4g.small` (or `t3.micro` x86)  
5. Key pair: create or select one (`.pem` file)  
6. Security group `lawn-ec2-sg`:  
   - SSH (22) from **My IP**  
   - Custom TCP **8081**, **8082** from **0.0.0.0/0** *(learning; restrict later)*  
7. Launch  

### 3.2 Allow EC2 → RDS

Edit `lawn-rds-sg` inbound: PostgreSQL 5432 from **lawn-ec2-sg** (source = security group).

### 3.3 Install Java on EC2

```bash
ssh -i your-key.pem ec2-user@EC2_PUBLIC_IP

sudo dnf install -y java-17-amazon-corretto-devel maven git
java -version
```

### 3.4 Clone and build

```bash
git clone https://github.com/EdisonSky/courageous-li-lawn-care.git
cd courageous-li-lawn-care

mvn -DskipTests clean package
```

### 3.5 IAM role for S3 (recommended)

1. IAM → **Roles** → Create role → **EC2**  
2. Attach policy: `AmazonS3FullAccess` *(learning only; scope down to one bucket later)*  
3. EC2 → instance → **Actions** → Security → **Attach IAM role**  

### 3.6 Run services with AWS profile

**Terminal 1 — customer-service:**

```bash
export DB_URL=jdbc:postgresql://YOUR-RDS-ENDPOINT:5432/lawn_customers
export DB_USERNAME=lawn_admin
export DB_PASSWORD=your-password
export SPRING_PROFILES_ACTIVE=aws

java -jar customer-service/target/customer-service-1.0.0-SNAPSHOT.jar
```

**Terminal 2 — signup-service:**

```bash
export DB_URL=jdbc:postgresql://YOUR-RDS-ENDPOINT:5432/lawn_signups
export DB_USERNAME=lawn_admin
export DB_PASSWORD=your-password
export SPRING_PROFILES_ACTIVE=aws
export AWS_REGION=us-east-1
export S3_UPLOAD_BUCKET=your-uploads-bucket-name
export S3_ENABLED=true
export APP_CORS_ALLOWED_ORIGINS=http://YOUR-WEBSITE-URL

java -jar signup-service/target/signup-service-1.0.0-SNAPSHOT.jar
```

Use `screen` or `systemd` to keep them running after you disconnect.

---

## Part 4 — Deploy React UI to S3

On your Mac:

```bash
cd lawn-ui
cp .env.production.example .env.production
# Edit VITE_API_BASE to http://EC2_PUBLIC_IP:8082

npm install
npm run build

export WEBSITE_BUCKET=your-website-bucket-name
aws s3 sync dist/ s3://$WEBSITE_BUCKET --delete
```

Open the S3 **Static website hosting** URL from the bucket properties.

### Optional: CloudFront + HTTPS

1. CloudFront → Create distribution  
2. Origin: S3 website endpoint  
3. Default root object: `index.html`  
4. Add error page: 403 → `/index.html` (SPA routing)  

Point `VITE_API_BASE` to your EC2 URL (or an Application Load Balancer later).

---

## Part 5 — Test end-to-end

1. Open the S3/CloudFront website URL  
2. Submit a signup  
3. Upload a lawn photo on the success screen  
4. Verify RDS:

```bash
psql -h $RDS_HOST -U lawn_admin -d lawn_signups -c "SELECT id, lawn_photo_key FROM signups;"
```

5. Verify S3 uploads bucket in AWS Console → S3 → browse `signups/` prefix  

---

## Environment variables cheat sheet

| Variable | Service | Example |
|----------|---------|---------|
| `DB_URL` | both | `jdbc:postgresql://lawn-db.xxx.rds.amazonaws.com:5432/lawn_customers` |
| `DB_USERNAME` | both | `lawn_admin` |
| `DB_PASSWORD` | both | *(secret)* |
| `SPRING_PROFILES_ACTIVE` | both | `aws` |
| `S3_UPLOAD_BUCKET` | signup | `courageous-li-lawn-uploads-123456` |
| `S3_ENABLED` | signup | `true` |
| `AWS_REGION` | signup | `us-east-1` |
| `APP_CORS_ALLOWED_ORIGINS` | signup | `http://bucket.s3-website-us-east-1.amazonaws.com` |
| `VITE_API_BASE` | lawn-ui build | `http://ec2-xx-xx-xx-xx.compute.amazonaws.com:8082` |

---

## Cost tips (learning)

- Stop EC2 when not using it  
- Use RDS free tier if eligible  
- Delete unused buckets and snapshots  
- Set billing alerts in AWS Budgets  

---

## Next improvements

- Application Load Balancer + single API URL  
- ECS Fargate instead of manual EC2  
- Secrets Manager for DB password  
- Private RDS + EC2 in same VPC  
- Route 53 custom domain + ACM certificate  

import { useState } from 'react';
import { uploadSignupPhoto } from '../api/signupApi';
import type { SignupResult } from '../types';
import { SERVICE_OPTIONS } from '../types';

interface SignupSuccessProps {
  result: SignupResult;
  onReset: () => void;
}

function serviceLabel(value: SignupResult['serviceType']) {
  return SERVICE_OPTIONS.find((o) => o.value === value)?.label ?? value;
}

export function SignupSuccess({ result, onReset }: SignupSuccessProps) {
  const [photoUrl, setPhotoUrl] = useState<string | null>(result.lawnPhotoUrl);
  const [uploading, setUploading] = useState(false);
  const [uploadError, setUploadError] = useState<string | null>(null);

  async function handlePhotoChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;

    setUploading(true);
    setUploadError(null);
    try {
      const uploaded = await uploadSignupPhoto(result.id, file);
      setPhotoUrl(uploaded.lawnPhotoUrl);
    } catch (err) {
      setUploadError(err instanceof Error ? err.message : 'Upload failed');
    } finally {
      setUploading(false);
    }
  }

  return (
    <div className="success-card">
      <div className="success-icon">✓</div>
      <h2>Signup received!</h2>
      <p className="success-lead">
        Thanks for signing up. We saved your request and will follow up soon.
      </p>
      <dl className="success-details">
        <div>
          <dt>Signup ID</dt>
          <dd>{result.id}</dd>
        </div>
        <div>
          <dt>Customer ID</dt>
          <dd>{result.customerId}</dd>
        </div>
        <div>
          <dt>Service</dt>
          <dd>{serviceLabel(result.serviceType)}</dd>
        </div>
        <div>
          <dt>Lot size</dt>
          <dd>{result.lotSizeSqFt.toLocaleString()} sq ft</dd>
        </div>
        <div>
          <dt>Start date</dt>
          <dd>{result.preferredStartDate}</dd>
        </div>
        <div>
          <dt>Status</dt>
          <dd>{result.status}</dd>
        </div>
      </dl>

      <section className="photo-upload">
        <h3>Optional: upload a lawn photo</h3>
        <p className="photo-hint">
          On AWS, this file is stored in S3 under{' '}
          <code>signups/{result.id}/</code>
        </p>
        <input
          type="file"
          accept="image/*"
          onChange={handlePhotoChange}
          disabled={uploading}
        />
        {uploading && <p className="photo-status">Uploading…</p>}
        {uploadError && <p className="error-banner">{uploadError}</p>}
        {photoUrl && (
          <p className="photo-status">
            Saved to S3:{' '}
            <a href={photoUrl} target="_blank" rel="noreferrer">
              view photo
            </a>
          </p>
        )}
      </section>

      <button className="submit-btn secondary" type="button" onClick={onReset}>
        Submit another signup
      </button>
    </div>
  );
}

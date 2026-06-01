package com.example.lawn.signup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "signups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;

    @Column(nullable = false)
    private int lotSizeSqFt;

    @Column(nullable = false)
    private LocalDate preferredStartDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignupStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    /** S3 object key, e.g. signups/1/lawn.jpg */
    private String lawnPhotoKey;
}

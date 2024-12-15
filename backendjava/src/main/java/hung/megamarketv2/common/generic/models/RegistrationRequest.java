package hung.megamarketv2.common.generic.models;

import java.time.LocalDateTime;

import hung.megamarketv2.common.generic.enums.UserRole;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.EmailVerificationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.PhoneNumberVerificationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.RegistrationStatus;
import hung.megamarketv2.common.generic.enums.RegistrationEnums.RegistrationSteps;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RegistrationRequest extends BaseModel {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Email
    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailVerificationStatus emailVerificationStatus = EmailVerificationStatus.NOT_VERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PhoneNumberVerificationStatus phoneNumberVerificationStatus = PhoneNumberVerificationStatus.NOT_VERIFIED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.PROCESSING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationSteps step = RegistrationSteps.VERIFY_EMAIL_OTP;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public RegistrationRequest(String email, UserRole role, LocalDateTime expiredAt) {
        this.email = email;
        this.role = role;
        this.expiredAt = expiredAt;
    }

}

package hung.megamarketv2.common.generic.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Immutable;
import hung.megamarketv2.common.generic.enums.OtpEnums.OtpStatus;
import hung.megamarketv2.common.generic.enums.OtpEnums.OtpType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OtpRequest extends BaseModel {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpStatus status = OtpStatus.ACTIVE;

    @Column(nullable = false)
    private String hashedOtp;

    @Column(nullable = false)
    private String referenceType;

    @Column(nullable = false)
    private String referenceId;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int validationAttempts;

    @Column(nullable = false)
    @Immutable
    private LocalDateTime sentAt;

    @Column(nullable = false)
    @Immutable
    private LocalDateTime canResendAt;

    @Column(nullable = false)
    @Immutable
    private LocalDateTime expiredAt;

    public OtpRequest(OtpType type, String hashedOtp, String referenceType, String referenceId,
            LocalDateTime canResendAt,
            LocalDateTime expiredAt) {
        this.type = type;
        this.hashedOtp = hashedOtp;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.canResendAt = canResendAt;
        this.expiredAt = expiredAt;
    }

    @PrePersist
    public void assignSentAt() {
        sentAt = LocalDateTime.now();
    }

}

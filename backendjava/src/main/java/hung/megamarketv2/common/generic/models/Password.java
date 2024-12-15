package hung.megamarketv2.common.generic.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = "user")
@NoArgsConstructor
public class Password extends BaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private String value;

    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean isEnabled = true;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public Password(User user, String value, LocalDateTime expiredAt) {
        this.user = user;
        this.value = value;
        this.expiredAt = expiredAt;
    }

}

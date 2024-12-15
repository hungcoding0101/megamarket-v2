package hung.megamarketv2.common.generic.models;

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
public class Account extends BaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true, nullable = false)
    private User user;

    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean isEnabled = true;

    public Account(User user) {
        this.user = user;
    }

}

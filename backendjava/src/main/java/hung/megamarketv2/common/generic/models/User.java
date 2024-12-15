package hung.megamarketv2.common.generic.models;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import hung.megamarketv2.common.generic.enums.UserRole;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Table(name = "app_user")
@NoArgsConstructor
public class User extends BaseModel {

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean isEnabled = true;

    public User(String email, String phoneNumber, UserRole role) {
        this.role = role;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}

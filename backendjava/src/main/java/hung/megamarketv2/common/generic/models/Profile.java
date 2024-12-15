package hung.megamarketv2.common.generic.models;

import java.time.LocalDate;

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
@EqualsAndHashCode(callSuper = true, exclude = { "user", "avatar" })
@NoArgsConstructor
public class Profile extends BaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true, nullable = false)
    private User user;

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY)
    private Avatar avatar;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate dateOfBirth;

    public Profile(User user, String firstName, String lastName) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

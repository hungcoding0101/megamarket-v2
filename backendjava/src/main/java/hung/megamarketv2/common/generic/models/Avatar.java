package hung.megamarketv2.common.generic.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = { "profile", "image" })
public class Avatar extends BaseModel {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private Profile profile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private StaticResource image;
}

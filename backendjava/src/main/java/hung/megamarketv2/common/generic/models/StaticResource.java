package hung.megamarketv2.common.generic.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class StaticResource extends BaseModel {
    @Column(unique = true, nullable = false)
    private String fileName;
}

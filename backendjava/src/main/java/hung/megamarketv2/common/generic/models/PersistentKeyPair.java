package hung.megamarketv2.common.generic.models;

import java.sql.Types;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PersistentKeyPair extends BaseModel {
    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] publicKeyBytes;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] privateKeyBytes;

}

package hung.megamarketv2.common.generic.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.Immutable;
import hung.megamarketv2.common.generic.constants.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseModel {

    @Id
    @Setter(AccessLevel.NONE)
    @SequenceGenerator(name = ModelConstants.SEQUENCE_GENERATOR_NAME, sequenceName = ModelConstants.SEQUENCE_NAME, allocationSize = ModelConstants.SEQUENCE_ALLOCATION_SIZE)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ModelConstants.SEQUENCE_GENERATOR_NAME)
    private long id;

    @Column(unique = true, nullable = false)
    @Immutable
    private String textId;

    @Version
    @Setter(AccessLevel.NONE)
    private long currentVersion;

    @Column(nullable = false, updatable = false)
    @Immutable
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        UUID uuid = UUID.randomUUID();
        textId = uuid.toString();
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}

package hung.megamarketv2.ecommerce.modules.security.repositories;

import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hung.megamarketv2.common.generic.models.PersistentKeyPair;
import hung.megamarketv2.common.generic.models.QPersistentKeyPair;
import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.KeyPairRepositoryErrorCodes;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class KeyPairRepositoryImp implements KeyPairRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Result<PersistentKeyPair, KeyPairRepositoryErrorCodes> findLatest() {
        QPersistentKeyPair qKeyPair = QPersistentKeyPair.persistentKeyPair;
        OrderSpecifier<LocalDateTime> latestCreatedAt = qKeyPair.createdAt.desc();

        PersistentKeyPair latestKeyPair = queryFactory.selectFrom(qKeyPair).orderBy(latestCreatedAt).fetchFirst();

        if (latestKeyPair == null) {
            return Result.ofError(KeyPairRepositoryErrorCodes.KEY_PAIR_NOT_FOUND);
        }

        return Result.ofValue(latestKeyPair);
    }

    @Override
    public Outcome<KeyPairRepositoryErrorCodes> create(PersistentKeyPair keyPair) {
        try {
            entityManager.persist(keyPair);

            return Outcome.ofSuccess();
        } catch (EntityExistsException e) {
            return Outcome.ofError(KeyPairRepositoryErrorCodes.KEY_PAIR_ALREADY_EXISTS);
        }
    }

}

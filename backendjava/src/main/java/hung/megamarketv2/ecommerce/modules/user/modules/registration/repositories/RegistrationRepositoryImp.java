package hung.megamarketv2.ecommerce.modules.user.modules.registration.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hung.megamarketv2.common.generic.models.QRegistrationRequest;
import hung.megamarketv2.common.generic.models.RegistrationRequest;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationRepositoryErrorCodes;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RegistrationRepositoryImp implements RegistrationRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> findtById(long id) {
        RegistrationRequest request = entityManager.find(RegistrationRequest.class, id);

        if (request == null) {
            return Result.ofError(RegistrationRepositoryErrorCodes.REQUEST_NOT_FOUND);
        }

        return Result.ofValue(request);
    }

    @Override
    public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> findtByTextId(String textId) {
        QRegistrationRequest qRequest = QRegistrationRequest.registrationRequest;

        RegistrationRequest request = queryFactory.selectFrom(qRequest).where(qRequest.textId.eq(textId)).fetchOne();

        if (request == null) {
            return Result.ofError(RegistrationRepositoryErrorCodes.REQUEST_NOT_FOUND);
        }

        return Result.ofValue(request);
    }

    @Override
    public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> createRegistrationRequest(
            RegistrationRequest request) {
        try {
            entityManager.persist(request);

            return Result.ofValue(request);
        } catch (EntityExistsException e) {
            return Result.ofError(RegistrationRepositoryErrorCodes.REQUEST_ALREADY_EXISTS);
        }
    }

    @Override
    public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> findtByTextIdThenLockRequestInDatabase(
            String textId) {
        QRegistrationRequest qRegistrationRequest = QRegistrationRequest.registrationRequest;

        RegistrationRequest registrationRequest = queryFactory.selectFrom(qRegistrationRequest)
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .where(qRegistrationRequest.textId.eq(textId)).fetchOne();

        if (registrationRequest == null) {
            return Result.ofError(RegistrationRepositoryErrorCodes.REQUEST_NOT_FOUND);
        }

        return Result.ofValue(registrationRequest);
    }

}

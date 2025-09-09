package hung.megamarketv2.common.otp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hung.megamarketv2.common.generic.models.OtpRequest;
import hung.megamarketv2.common.generic.models.QOtpRequest;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.otp.ErrorCodes.OtpRepositoryErrorCodes;
import hung.megamarketv2.common.otp.Dto.OtpIdentity;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class OtpRepositoryImp implements OtpRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Result<Optional<OtpRequest>, OtpRepositoryErrorCodes> findById(long id) {
        OtpRequest otpRequest = entityManager.find(OtpRequest.class, id);

        Optional<OtpRequest> result = Optional.ofNullable(otpRequest);

        return Result.ofValue(result);
    }

    @Override
    public Result<Optional<OtpRequest>, OtpRepositoryErrorCodes> findByTextId(String textId) {
        QOtpRequest qOtpRequest = QOtpRequest.otpRequest;

        OtpRequest otpRequest = queryFactory.selectFrom(qOtpRequest)
                .where(qOtpRequest.textId.eq(textId))
                .fetchOne();

        Optional<OtpRequest> result = Optional.ofNullable(otpRequest);

        return Result.ofValue(result);
    }

    @Override
    public Result<List<OtpRequest>, OtpRepositoryErrorCodes> findByIdentity(OtpIdentity otpIdentity) {
        QOtpRequest qOtpRequest = QOtpRequest.otpRequest;
        BooleanExpression equalReferenceId = qOtpRequest.referenceId.eq(otpIdentity.referenceId());
        BooleanExpression equalReferenceType = qOtpRequest.referenceType.eq(otpIdentity.referenceType());
        BooleanExpression equalOtpType = qOtpRequest.type.eq(otpIdentity.otpType());

        List<OtpRequest> otpRequest = queryFactory.selectFrom(qOtpRequest)
                .where(equalReferenceId
                        .and(equalReferenceType)
                        .and(equalOtpType))
                .fetch();

        return Result.ofValue(otpRequest);
    }

    @Override
    public Result<OtpRequest, OtpRepositoryErrorCodes> createOtpRequest(OtpRequest otpRequest) {
        try {
            entityManager.persist(otpRequest);

            return Result.ofValue(otpRequest);
        } catch (EntityExistsException e) {
            return Result.ofError(OtpRepositoryErrorCodes.OTP_REQUEST_ALREADY_EXISTS);
        }
    }

}

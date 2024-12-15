package hung.megamarketv2.common.password.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hung.megamarketv2.common.generic.models.Password;
import hung.megamarketv2.common.generic.models.QPassword;
import hung.megamarketv2.common.generic.models.User;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.password.ErrorCodes.PasswordRepositoryErrorCodes;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLTemplates;
import jakarta.annotation.PostConstruct;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class PasswordRepositoryImp implements PasswordRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Result<Password, PasswordRepositoryErrorCodes> findById(Long id) {
        Password password = entityManager.find(Password.class, id);

        if (password == null) {
            return Result.ofError(PasswordRepositoryErrorCodes.PASSWORD_NOT_FOUND);
        }

        return Result.ofValue(password);
    }

    @Override
    public Result<Password, PasswordRepositoryErrorCodes> findByTextId(String textId) {
        QPassword qPassword = QPassword.password;

        Password password = queryFactory.selectFrom(qPassword).where(qPassword.textId.eq(textId)).fetchOne();

        if (password == null) {
            return Result.ofError(PasswordRepositoryErrorCodes.PASSWORD_NOT_FOUND);
        }

        return Result.ofValue(password);
    }

    @Override
    public Result<Password, PasswordRepositoryErrorCodes> findEnabledPasswordByUser(User user) {
        QPassword qPassword = QPassword.password;

        BooleanExpression isUserEqual = qPassword.user.eq(user);
        BooleanExpression isEnabled = qPassword.isEnabled.eq(true);

        Password password = queryFactory.selectFrom(qPassword).where(isUserEqual.and(isEnabled)).fetchOne();

        if (password == null) {
            return Result.ofError(PasswordRepositoryErrorCodes.PASSWORD_NOT_FOUND);
        }

        return Result.ofValue(password);
    }

    @Override
    public Result<Password, PasswordRepositoryErrorCodes> createPassword(Password password) {
        try {
            entityManager.persist(password);
            return Result.ofValue(password);
        } catch (EntityExistsException e) {
            return Result.ofError(PasswordRepositoryErrorCodes.PASSWORD_ALREADY_EXISTS);
        }
    }

}
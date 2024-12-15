package hung.megamarketv2.common.user.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hung.megamarketv2.common.generic.models.User;
import hung.megamarketv2.common.generic.models.QUser;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.user.ErrorCodes.UserRepositoryErrorCodes;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class UserRepositoryImp implements UserRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Result<User, UserRepositoryErrorCodes> findById(Long id) {
        User user = entityManager.find(User.class, id);

        if (user == null) {
            return Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND);
        }

        return Result.ofValue(user);
    }

    @Override
    public Result<User, UserRepositoryErrorCodes> findByTextId(String textId) {
        QUser qUser = QUser.user;
        User user = queryFactory.selectFrom(qUser).where(qUser.textId.eq(textId)).fetchOne();

        if (user == null) {
            return Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND);
        }

        return Result.ofValue(user);
    }

    @Override
    public Result<User, UserRepositoryErrorCodes> createUser(User user) {
        try {
            entityManager.persist(user);
            return Result.ofValue(user);
        } catch (EntityExistsException e) {
            return Result.ofError(UserRepositoryErrorCodes.USER_ALREADY_EXISTS);
        }
    }

    @Override
    public Result<User, UserRepositoryErrorCodes> findByEmail(String email) {
        QUser qUser = QUser.user;
        User user = queryFactory.selectFrom(qUser).where(qUser.email.eq(email)).fetchOne();

        if (user == null) {
            return Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND);
        }

        return Result.ofValue(user);
    }

    @Override
    public Result<User, UserRepositoryErrorCodes> findByPhoneNumber(String phoneNumber) {
        QUser qUser = QUser.user;
        User user = queryFactory.selectFrom(qUser).where(qUser.phoneNumber.eq(phoneNumber)).fetchOne();

        if (user == null) {
            return Result.ofError(UserRepositoryErrorCodes.USER_NOT_FOUND);
        }

        return Result.ofValue(user);
    }

}

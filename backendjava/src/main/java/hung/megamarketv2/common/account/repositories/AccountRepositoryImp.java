package hung.megamarketv2.common.account.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;

import hung.megamarketv2.common.account.ErrorCodes.AccountRepositoryErrorCodes;
import hung.megamarketv2.common.generic.models.Account;
import hung.megamarketv2.common.generic.models.QAccount;
import hung.megamarketv2.common.generic.results.Result;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class AccountRepositoryImp implements AccountRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    @PostConstruct
    public void init() {
        queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public Result<Account, AccountRepositoryErrorCodes> findtById(long id) {
        Account account = entityManager.find(Account.class, id);

        if (account == null) {
            return Result.ofError(AccountRepositoryErrorCodes.ACCOUNT_NOT_FOUND);
        }

        return Result.ofValue(account);
    }

    @Override
    public Result<Account, AccountRepositoryErrorCodes> findtByTextId(String textId) {
        QAccount qAccount = QAccount.account;
        Account account = queryFactory.selectFrom(qAccount).where(qAccount.textId.eq(textId)).fetchOne();

        if (account == null) {
            return Result.ofError(AccountRepositoryErrorCodes.ACCOUNT_NOT_FOUND);
        }

        return Result.ofValue(account);
    }

    @Override
    public Result<Account, AccountRepositoryErrorCodes> lockAccountInDatabase(long id) {
        QAccount qAccount = QAccount.account;
        Account account = queryFactory.selectFrom(qAccount).setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .where(qAccount.id.eq(id)).fetchOne();

        if (account == null) {
            return Result.ofError(AccountRepositoryErrorCodes.ACCOUNT_NOT_FOUND);
        }

        return Result.ofValue(account);
    }

    @Override
    public Result<Account, AccountRepositoryErrorCodes> createAccount(Account account) {
        try {
            entityManager.persist(account);

            return Result.ofValue(account);
        } catch (EntityExistsException e) {
            return Result.ofError(AccountRepositoryErrorCodes.ACCOUNT_ALREADY_EXISTS);
        }
    }

}

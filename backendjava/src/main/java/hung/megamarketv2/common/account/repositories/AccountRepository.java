package hung.megamarketv2.common.account.repositories;

import hung.megamarketv2.common.account.ErrorCodes.AccountRepositoryErrorCodes;
import hung.megamarketv2.common.generic.models.Account;
import hung.megamarketv2.common.generic.results.Result;

public interface AccountRepository {

    public Result<Account, AccountRepositoryErrorCodes> findtById(long id);

    public Result<Account, AccountRepositoryErrorCodes> findtByTextId(String textId);

    public Result<Account, AccountRepositoryErrorCodes> lockAccountInDatabase(long id);

    public Result<Account, AccountRepositoryErrorCodes> createAccount(Account account);

}

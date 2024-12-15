package hung.megamarketv2.common.password.repositories;

import hung.megamarketv2.common.generic.models.Password;
import hung.megamarketv2.common.generic.models.User;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.password.ErrorCodes.PasswordRepositoryErrorCodes;

public interface PasswordRepository {
    public Result<Password, PasswordRepositoryErrorCodes> findById(Long id);

    public Result<Password, PasswordRepositoryErrorCodes> findByTextId(String textId);

    public Result<Password, PasswordRepositoryErrorCodes> createPassword(Password password);

    public Result<Password, PasswordRepositoryErrorCodes> findEnabledPasswordByUser(User user);
}

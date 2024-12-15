package hung.megamarketv2.common.user.repositories;

import hung.megamarketv2.common.generic.models.User;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.common.user.ErrorCodes.UserRepositoryErrorCodes;

public interface UserRepository {

    public Result<User, UserRepositoryErrorCodes> findById(Long id);

    public Result<User, UserRepositoryErrorCodes> findByTextId(String textId);

    public Result<User, UserRepositoryErrorCodes> findByEmail(String email);

    public Result<User, UserRepositoryErrorCodes> findByPhoneNumber(String phoneNumber);

    public Result<User, UserRepositoryErrorCodes> createUser(User user);

}

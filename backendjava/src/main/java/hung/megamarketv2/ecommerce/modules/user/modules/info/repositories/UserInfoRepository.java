package hung.megamarketv2.ecommerce.modules.user.modules.info.repositories;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.user.modules.info.Dto.UserInfo;
import hung.megamarketv2.ecommerce.modules.user.modules.info.ErrorCodes.UserInfoRepositoryErrorCodes;

public interface UserInfoRepository {

    Result<UserInfo, UserInfoRepositoryErrorCodes> findUserInfo(long userId);
}

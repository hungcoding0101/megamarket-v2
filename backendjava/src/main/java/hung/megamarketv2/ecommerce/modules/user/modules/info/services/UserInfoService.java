package hung.megamarketv2.ecommerce.modules.user.modules.info.services;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.user.modules.info.Dto.UserInfo;
import hung.megamarketv2.ecommerce.modules.user.modules.info.ErrorCodes.UserInfoServiceErrorCodes;

public interface UserInfoService {

    public Result<UserInfo, UserInfoServiceErrorCodes> getUserInfo(long userId);

    public Outcome<UserInfoServiceErrorCodes> updateAddress(long userId, String address);

    public Outcome<UserInfoServiceErrorCodes> updateFirstName(long userId, String firstName);

    public Outcome<UserInfoServiceErrorCodes> updateLastName(long userId, String lastName);

    public Outcome<UserInfoServiceErrorCodes> updateDateOfBirth(long userId, LocalDateTime dateOfBirth);

    public Outcome<UserInfoServiceErrorCodes> updateDateOfBirth(long userId, MultipartFile avatar);

    public Outcome<UserInfoServiceErrorCodes> updateDateEmail(long userId, String email);

    public Outcome<UserInfoServiceErrorCodes> updateDatePhoneNumber(long userId, String phoneNumber);
}

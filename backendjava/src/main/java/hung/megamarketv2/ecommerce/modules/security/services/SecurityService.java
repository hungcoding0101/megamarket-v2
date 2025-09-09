package hung.megamarketv2.ecommerce.modules.security.services;

import java.util.List;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.SecurityServiceErrorCodes;

public interface SecurityService {

    public Result<String, SecurityServiceErrorCodes> getAccessTokenForUser(Long userId, List<String> roles);

}

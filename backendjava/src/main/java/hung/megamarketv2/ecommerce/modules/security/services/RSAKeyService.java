package hung.megamarketv2.ecommerce.modules.security.services;

import com.nimbusds.jose.jwk.RSAKey;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.RSAKeyServiceErrorCodes;

public interface RSAKeyService {

    public Result<RSAKey, RSAKeyServiceErrorCodes> getLatestKey();

    public Result<RSAKey, RSAKeyServiceErrorCodes> createNewKey();
}

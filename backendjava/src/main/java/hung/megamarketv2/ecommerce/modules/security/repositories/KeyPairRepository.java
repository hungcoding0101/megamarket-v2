package hung.megamarketv2.ecommerce.modules.security.repositories;

import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.security.ErrorCodes.KeyPairRepositoryErrorCodes;

import hung.megamarketv2.common.generic.models.PersistentKeyPair;
import hung.megamarketv2.common.generic.outcomes.Outcome;

public interface KeyPairRepository {

    Result<PersistentKeyPair, KeyPairRepositoryErrorCodes> findLatest();

    Outcome<KeyPairRepositoryErrorCodes> create(PersistentKeyPair keyPair);
}

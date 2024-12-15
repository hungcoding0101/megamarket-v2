package hung.megamarketv2.ecommerce.modules.user.modules.registration.repositories;

import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationRepositoryErrorCodes;

import hung.megamarketv2.common.generic.models.RegistrationRequest;
import hung.megamarketv2.common.generic.results.Result;

public interface RegistrationRepository {
        public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> findtById(long id);

        public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> findtByTextId(String textId);

        public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> findtByTextIdThenLockRequestInDatabase(
                        String textId);

        public Result<RegistrationRequest, RegistrationRepositoryErrorCodes> createRegistrationRequest(
                        RegistrationRequest request);
}

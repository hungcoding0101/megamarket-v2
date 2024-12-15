package hung.megamarketv2.ecommerce.modules.user.modules.registration.services;

import hung.megamarketv2.common.generic.enums.UserRole;
import hung.megamarketv2.common.generic.outcomes.Outcome;
import hung.megamarketv2.common.generic.results.Result;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.Dto.EmailSubmissionResult;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes;

public interface RegistrationService {

        public Result<EmailSubmissionResult, RegistrationServiceErrorCodes> submitEmail(String email,
                        UserRole role);

        public Outcome<RegistrationServiceErrorCodes> verifyEmailOtp(String otp, String registrationToken);

        public Outcome<RegistrationServiceErrorCodes> submitPhoneNumber(String phoneNumber,
                        String registrationToken);

        public Outcome<RegistrationServiceErrorCodes> submitPassword(String password,
                        String registrationToken);

}

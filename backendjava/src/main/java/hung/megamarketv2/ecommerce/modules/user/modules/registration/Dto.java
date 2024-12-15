package hung.megamarketv2.ecommerce.modules.user.modules.registration;

import hung.megamarketv2.common.generic.enums.UserRole;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes.StringRepresentation;
import jakarta.validation.constraints.NotNull;

public class Dto {

        public static record EmailSubmissionRequest(
                        @NotNull(message = StringRepresentation.MISSING_EMAIL)
                        String email,

                        @NotNull(message = StringRepresentation.MISSING_ROLE)
                        UserRole role) {
        }

        public static record EmailSubmissionResult(
                        @NotNull(message = StringRepresentation.MISSING_OTP_COOLDOWN_SECONDS)
                        int otpCooldownSeconds,
                                        
                        @NotNull(message = StringRepresentation.MISSING_REGISTRATION_TOKEN)
                        String registrationToken) {
        }

        public static record EmailVerificationRequest(
                        @NotNull(message = StringRepresentation.MISSING_OTP)
                        String otp,
                                        
                        @NotNull(message = StringRepresentation.MISSING_REGISTRATION_TOKEN)
                        String registrationToken) {
        }

        public static record PhoneNumberSubmissionRequest(
                        @NotNull(message = StringRepresentation.MISSING_PHONE_NUMBER)
                        String phoneNumber,
                                                        
                        @NotNull(message = StringRepresentation.MISSING_REGISTRATION_TOKEN)
                        String registrationToken) {
        }

        public static record PasswordSubmissionRequest(
                        @NotNull(message = StringRepresentation.MISSING_PASSWORD)
                        String password,
                                                        
                        @NotNull(message = StringRepresentation.MISSING_REGISTRATION_TOKEN)
                        String registrationToken) {
        }

}

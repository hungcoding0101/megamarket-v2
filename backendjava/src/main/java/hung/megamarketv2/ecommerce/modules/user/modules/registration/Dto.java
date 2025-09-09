package hung.megamarketv2.ecommerce.modules.user.modules.registration;

import hung.megamarketv2.common.generic.enums.UserRole;
import hung.megamarketv2.ecommerce.modules.user.modules.registration.ErrorCodes.RegistrationServiceErrorCodes.StringRepresentation;
import jakarta.validation.constraints.NotNull;

public class Dto {

        public static record EmailSubmissionRequest(
                        @NotNull
                        String email,

                        @NotNull
                        UserRole role) {
        }

        public static record EmailSubmissionResult(
                        @NotNull int otpCooldownSeconds,

                        @NotNull String registrationToken) {
        }
        
        public static record EmailOtpSendingResult(
                        @NotNull
                        int otpCooldownSeconds) {
        }

        public static record EmailVerificationRequest(
                        @NotNull
                        String otp,
                                        
                        @NotNull
                        String registrationToken) {
        }

        public static record PhoneNumberSubmissionRequest(
                        @NotNull
                        String phoneNumber,
                                                        
                        @NotNull
                        String registrationToken) {
        }

        public static record PasswordSubmissionRequest(
                        @NotNull
                        String password,
                                                        
                        @NotNull
                        String registrationToken) {
        }
        public static record PasswordSubmissionResult(
                       String accessToken) {
        }

}

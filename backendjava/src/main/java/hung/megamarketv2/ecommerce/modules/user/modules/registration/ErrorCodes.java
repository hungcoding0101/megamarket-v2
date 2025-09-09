package hung.megamarketv2.ecommerce.modules.user.modules.registration;

public final class ErrorCodes {

    public enum RegistrationServiceErrorCodes {
        INVALID_EMAIL,
        INVALID_PASSWORD,
        INVALID_PHONE_NUMBER,
        MISSING_EMAIL,
        MISSING_PHONE_NUMBER,
        MISSING_ROLE,
        MISSING_OTP,
        MISSING_PASSWORD,
        MISSING_OTP_COOLDOWN_SECONDS,
        MISSING_REGISTRATION_TOKEN,
        USER_NOT_FOUND,
        EMAIL_HAS_BEEN_USED,
        EMAIL_NOT_VERIFIED,
        PHONE_NUMBER_HAS_BEEN_USED,
        RESEND_OTP_TOO_FREQUENTLY,
        REQUEST_NOT_FOUND,
        REQUEST_EXPIRED,
        INVALID_REQUEST_STATUS,
        INVALID_REQUEST_STEP,
        OTP_EXPIRED,
        OTP_NOT_MATCHED,
        REACHED_OTP_ATTEMPT_LIMIT,
        UNABLE_TO_GET_ACCESS_TOKEN;

        public static final class StringRepresentation {
            public static final String INVALID_EMAIL = "INVALID_EMAIL";
            public static final String MISSING_EMAIL = "MISSING_EMAIL";
            public static final String MISSING_PHONE_NUMBER = "MISSING_PHONE_NUMBER";
            public static final String MISSING_ROLE = "MISSING_ROLE";
            public static final String MISSING_OTP = "MISSING_OTP";
            public static final String MISSING_OTP_COOLDOWN_SECONDS = "MISSING_OTP_COOLDOWN_SECONDS";
            public static final String MISSING_REGISTRATION_TOKEN = "MISSING_REGISTRATION_TOKEN";
            public static final String MISSING_PASSWORD = "MISSING_PASSWORD";

            private StringRepresentation() {
            }
        }
    }

    public enum RegistrationRepositoryErrorCodes {
        REQUEST_NOT_FOUND,
        REQUEST_ALREADY_EXISTS
    }

    private ErrorCodes() {
    }
}

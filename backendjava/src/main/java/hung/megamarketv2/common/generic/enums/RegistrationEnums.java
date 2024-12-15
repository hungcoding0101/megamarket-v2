package hung.megamarketv2.common.generic.enums;

public final class RegistrationEnums {

    public enum RegistrationStatus {
        PROCESSING, APPROVED, REJECTED
    }

    public enum RegistrationSteps {
        SUBMIT_EMAIL, VERIFY_EMAIL_OTP, SUBMIT_PHONE_NUMBER, SUBMIT_PASSWORD, COMPLETED
    }

    public enum EmailVerificationStatus {
        NOT_VERIFIED,
        VERIFIED

    }

    public enum PhoneNumberVerificationStatus {
        NOT_VERIFIED,
        VERIFIED

    }

    private RegistrationEnums() {
    }
}

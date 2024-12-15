package hung.megamarketv2.common.otp;

public final class ErrorCodes {

    private ErrorCodes() {
    }

    public enum OtpRepositoryErrorCodes {
        ACCOUNT_NOT_FOUND,
        OTP_REQUEST_NOT_FOUND,
        OTP_REQUEST_ALREADY_EXISTS
    }

    public enum OtpServiceErrorCodes {
        ACCOUNT_NOT_FOUND,
        RESEND_OTP_TOO_FREQUENTLY,
        OTP_NOT_FOUND,
        OTP_EXPIRED,
        REACHED_ATTEMPT_LIMIT,
        OTP_TYPE_NOT_SUPPORTED,
        OTP_ALREADY_VERIFIED,
        OTP_NOT_MATCHED
    }
}

package hung.megamarketv2.common.otp;

import hung.megamarketv2.common.generic.enums.OtpEnums.OtpType;

public final class Dto {

    public static record Otp(String rawValue, String hashedValue) {
    }

    public static record OtpIdentity(OtpType otpType, String referenceType, String referenceId) {
    }

    public static record OtpSendingResult(String otpToken, int coolDownSeconds) {
    }

    private Dto() {
    }
}

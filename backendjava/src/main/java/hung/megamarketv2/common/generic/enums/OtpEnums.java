package hung.megamarketv2.common.generic.enums;

public final class OtpEnums {

    public enum OtpType {
        EMAIL, SMS
    }

    public enum OtpStatus {
        ACTIVE, EXPIRED, DEACTIVATED
    }

    private OtpEnums() {
    }

}

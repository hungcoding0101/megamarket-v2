package hung.megamarketv2.common.otp;

public final class Exceptions {

    public class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    private Exceptions() {
    }
}

package hung.megamarketv2.ecommerce.modules.security;

public final class Exceptions {
    public static class UnableToGetRSAKeyException extends RuntimeException {
        public UnableToGetRSAKeyException(String message) {
            super(message);
        }
    }

    private Exceptions() {
    }
}

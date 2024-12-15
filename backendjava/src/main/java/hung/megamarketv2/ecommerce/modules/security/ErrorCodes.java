package hung.megamarketv2.ecommerce.modules.security;

public final class ErrorCodes {

    public enum KeyPairRepositoryErrorCodes {
        KEY_PAIR_NOT_FOUND,
        KEY_PAIR_ALREADY_EXISTS
    }

    public enum RSAKeyServiceErrorCodes {
        KEY_NOT_FOUND,
        UNABLE_TO_CREATE_KEY,
        WRONG_KEY_ALGORITHM_NAME,
        INVALID_KEY_SPECIFICATION
    }

    private ErrorCodes() {
    }

}

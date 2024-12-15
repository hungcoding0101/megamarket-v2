package hung.megamarketv2.common.account;

public final class ErrorCodes {

    private ErrorCodes() {
    }

    public enum AccountRepositoryErrorCodes {
        ACCOUNT_NOT_FOUND,
        ACCOUNT_ALREADY_EXISTS
    }

    public enum AccountServiceErrorCodes {
        ACCOUNT_NOT_FOUND,
    }
}

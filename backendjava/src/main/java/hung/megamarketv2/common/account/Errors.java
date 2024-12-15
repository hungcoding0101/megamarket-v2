package hung.megamarketv2.common.account;

import hung.megamarketv2.common.account.ErrorCodes.AccountRepositoryErrorCodes;

public final class Errors {

    public static record AccountRepositoryError(AccountRepositoryErrorCodes errorCode) {
    }

    private Errors() {
    }

}

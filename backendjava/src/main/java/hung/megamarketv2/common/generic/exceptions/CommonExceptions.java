package hung.megamarketv2.common.generic.exceptions;

public final class CommonExceptions {

    public class UnknownErrorException extends RuntimeException {
    }

    public static class UnexpectedErrorException extends RuntimeException {
        public final String unexpectedError;

        public UnexpectedErrorException(String unexpectedError) {
            this.unexpectedError = unexpectedError;
        }
    }

    public static class AccountNotFoundException extends RuntimeException {
    }

    private CommonExceptions() {
    }
}

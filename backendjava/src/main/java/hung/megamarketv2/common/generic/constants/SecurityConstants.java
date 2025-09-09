package hung.megamarketv2.common.generic.constants;

public final class SecurityConstants {

    public static final String REGEX_PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";

    public static final String AUTHORIZATION = "Authorization";

    public static final String GRANT_TYPE = "grant_type";

    public static final String BEARER = "Bearer ";

    public static final String CLIENT_CREDENTIALS = "client_credentials";

    public static final String ACCESS_TOKEN = "access_token";

    public static final String ACCESS_TOKEN_PREFIX = "ACCESS_TOKEN_";

    public static final String REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN_";

    public static final String ROLE_PREFIX = "ROLE_";

    public static final String SUB = "sub";

    public static final String AUTHORITIES = "authorities";

    private SecurityConstants() {
    }
}

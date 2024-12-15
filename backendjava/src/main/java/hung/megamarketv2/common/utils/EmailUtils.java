package hung.megamarketv2.common.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailUtils {
    EmailValidator emailValidator;

    public EmailUtils() {
        emailValidator = EmailValidator.getInstance();
    }

    public boolean validateEmail(String email) {
        return emailValidator.isValid(email);
    }
}

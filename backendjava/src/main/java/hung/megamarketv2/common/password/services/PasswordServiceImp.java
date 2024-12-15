package hung.megamarketv2.common.password.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import hung.megamarketv2.common.generic.constants.SecurityConstants;

@Service
public class PasswordServiceImp implements PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordServiceImp(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(SecurityConstants.REGEX_PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

}

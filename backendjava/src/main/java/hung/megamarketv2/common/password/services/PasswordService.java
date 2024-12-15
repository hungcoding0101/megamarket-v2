package hung.megamarketv2.common.password.services;

public interface PasswordService {
    public String encodePassword(String rawPassword);

    public boolean validatePassword(String rawPassword);
}

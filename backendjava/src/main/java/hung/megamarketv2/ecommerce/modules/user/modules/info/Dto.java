package hung.megamarketv2.ecommerce.modules.user.modules.info;

import java.time.LocalDate;

public class Dto {

    public static record UserInfo(String email, String phoneNumber, String firstName,
             String lastName, String avatar, LocalDate dateOfBirth) {
    }
}

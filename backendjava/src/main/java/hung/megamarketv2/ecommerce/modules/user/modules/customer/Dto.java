package hung.megamarketv2.ecommerce.modules.user.modules.customer;

import java.time.LocalDate;

public final class Dto {

    public static record CustomerInfo(String email, String phoneNumber, String firstName, String lastName,
            String avatar, LocalDate dateOfBirth) {
    }

    private Dto() {

    }
}

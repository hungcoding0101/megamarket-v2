package hung.megamarketv2.integration.common.otp;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScans(value = {
        @ComponentScan("hung.megamarketv2.common.otp.repositories") })
public class TestConfig {

}

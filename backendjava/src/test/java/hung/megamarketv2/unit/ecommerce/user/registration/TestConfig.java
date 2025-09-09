package hung.megamarketv2.unit.ecommerce.user.registration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import hung.megamarketv2.MainTestConfig;
import hung.megamarketv2.ecommerce.modules.security.configs.SecurityConfig;

@Configuration
@Import({ SecurityConfig.class, MainTestConfig.class })
@ComponentScans(value = {
                @ComponentScan("hung.megamarketv2.ecommerce.modules.user.modules.registration.controllers"),
})
public class TestConfig {

}

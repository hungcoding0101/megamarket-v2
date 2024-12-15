package hung.megamarketv2;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EntityScan("hung.megamarketv2.common.generic.models")
@PropertySource("classpath:application.yaml")
public class MainConfig {

}

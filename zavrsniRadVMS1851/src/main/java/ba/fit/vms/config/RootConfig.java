package ba.fit.vms.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ComponentScan(basePackages = { "ba.fit.vms" })
public class RootConfig {
	
	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		ppc.setLocation(new ClassPathResource("/persistence.properties"));
		return ppc;
	}

}

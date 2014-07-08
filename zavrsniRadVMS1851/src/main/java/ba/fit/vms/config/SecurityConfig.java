package ba.fit.vms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

@Configuration
@ImportResource(value = "classpath:spring-security-context.xml")
public class SecurityConfig {
	
	@Bean
	public KorisnickiServis korisnickiServis(){
		return new KorisnickiServis();
	}
	
	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices("remember-me-key", korisnickiServis());
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new StandardPasswordEncoder();
	}

}

package ba.fit.vms.config;

import java.util.Collections;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;

public class KorisnickiServis implements UserDetailsService {
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@PostConstruct	
	protected void initialize() {
		/*
		Account adminAccount = new Account("admin", "Admin", "User", true, "123123", "ROLE_ADMIN");
		Account userAccount = new Account("user", "Regular", "User", true, "123123", "ROLE_USER");
		
		accountRepository.saveAccount(adminAccount);
		accountRepository.saveAccount(userAccount);
		*/
	}

	@Override
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		Korisnik korisnik = korisnikRepository.readByEmail(email);
		
		if(korisnik == null) {
			throw new UsernameNotFoundException("korisnik nije pronadjen!");
		}
		return kreirajUsera(korisnik);
	}
	
	public void signin(Korisnik korisnik){
		SecurityContextHolder.getContext().setAuthentication(autentificiraj(korisnik));
	}
	
	private Authentication autentificiraj(Korisnik korisnik) {
		return new UsernamePasswordAuthenticationToken(kreirajUsera(korisnik),  null, Collections.singleton(kreirajAuthority(korisnik)));
	}
	
	private User kreirajUsera(Korisnik korisnik){
		return new User(korisnik.getEmail(), korisnik.getLozinka(), Collections.singleton(kreirajAuthority(korisnik)));
	}
	
	private GrantedAuthority kreirajAuthority(Korisnik korisnik) {
		return new SimpleGrantedAuthority(korisnik.getRola());
	}

}

package ba.fit.vms.config;

import java.util.Collections;

// import javax.annotation.PostConstruct;
// import javax.inject.Inject;




import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
// import org.springframework.security.crypto.password.PasswordEncoder;


import org.springframework.security.crypto.password.PasswordEncoder;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;

public class KorisnickiServis implements UserDetailsService {
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	/**
	 * Dependency injection pasword enkodera kako bi smo inicijalne korisnike mogli ispravno upisati u bazu
	 **/ 
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@PostConstruct	
	protected void initialize() {
		try {
			Korisnik admin = korisnikRepository.find("root");
			System.out.println(admin.getEmail());
		} catch (Exception e) {
			Korisnik korisnikAdmin = new Korisnik("root", "Admin", "User", true, "toor", "ROLE_ADMIN");
			Korisnik korisnikRegular = new Korisnik("korisnik", "Regular", "User", true, "korisnik", "ROLE_USER");
			korisnikAdmin.setLozinka(passwordEncoder.encode(korisnikAdmin.getLozinka())); // vrsimo enkripciju lozinke
			korisnikRegular.setLozinka(passwordEncoder.encode(korisnikRegular.getLozinka())); // vrsimo enkripciju lozinke
			
			korisnikRepository.save(korisnikAdmin);
			korisnikRepository.save(korisnikRegular);
		}
		
		
	}

	@Override
	public UserDetails loadUserByUsername(String email)
			throws UsernameNotFoundException {
		Korisnik korisnik = korisnikRepository.find(email);
		
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

package ba.fit.vms.repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Korisnik;

@Repository
@Transactional(readOnly = true)
public class KorisnikRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	
	/**
	 * Metoda snima novog korisnika u bazu
	 * @param korisnik
	 * @return
	 */
	@Transactional
	public Korisnik save(Korisnik korisnik){
		korisnik.setLozinka(passwordEncoder.encode(korisnik.getLozinka())); // Enkripcija lozinke
		try {
			entityManager.persist(korisnik);
			return korisnik;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/**
	 * Metoda vraca korisnika sa odabranim email-om
	 * @param email
	 * @return
	 */
	public Korisnik read(String email){
		try {
			return entityManager.createNamedQuery(Korisnik.FIND_BY_EMAIL, Korisnik.class)
					.setParameter("email", email)
					.getSingleResult();
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/**
	 * Metoda vraca editovanog korisnika
	 * @param korisnik
	 * @return
	 */
	@Transactional
	public Korisnik edit(Korisnik korisnik){
		
		Korisnik stari = this.read(korisnik.getEmail());
		
		// Enkripcija lozinke
		if(korisnik.getLozinka().length()>0){
			korisnik.setLozinka(passwordEncoder.encode(korisnik.getLozinka()));
		} else {
			korisnik.setLozinka(stari.getLozinka());
		}
		
		/*
		entityManager.merge(account);
		entityManager.flush();
		return account;
		*/
		// editovanje se moze uraditi i ovako, medjutim da bi smo bili sigurni da su
		// svi podaci pravilno preneseni, korisnicemo BeanUtils copy opciju
		// koja ce kopirati sadrzaj polja iz jednog korisnika u drugog.
		
		BeanUtils.copyProperties(korisnik, stari);
		
		try {
			entityManager.merge(stari);
			return stari;
		} catch (PersistenceException e) {
			return null;
		}	
		
	}

}

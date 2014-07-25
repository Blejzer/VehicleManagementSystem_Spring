package ba.fit.vms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;

@Component("korisnikValidator")
public class KorisnikValidatorForme implements Validator{

	@Autowired
	private KorisnikRepository korisnikRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return Korisnik.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		Korisnik korisnik = (Korisnik)target;
		
		if(!korisnik.getEmail().isEmpty()){
			String email = korisnik.getEmail();
			
			Korisnik postoji = korisnikRepository.readByEmail(email);
			
			if (postoji !=null) {
				errors.rejectValue("email", "email.alreadyExist",
	                    "Korisnik sa ovom email adresom vec postoji");
			}
		}

	}



}

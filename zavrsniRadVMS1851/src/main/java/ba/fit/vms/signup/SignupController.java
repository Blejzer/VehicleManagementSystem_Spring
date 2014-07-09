package ba.fit.vms.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import ba.fit.vms.config.KorisnickiServis;
import ba.fit.vms.repository.KorisnikRepository;

@Controller
public class SignupController {
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private KorisnickiServis korisnickiServis;
	
	@RequestMapping(value = "signup")
	public SignupForm signup() {
		return new SignupForm();
	}
	
	

}

package ba.fit.vms.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ba.fit.vms.config.KorisnickiServis;
import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.signup.SignupForm;
import ba.fit.vms.support.web.PorukaHelper;

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
	
	/********************************************
	 * 					Mapiranje				*
	*********************************************/

	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return null;
		}
		
		Korisnik korisnik = korisnikRepository.save(signupForm.kreirajKorisnika());
		korisnickiServis.signin(korisnik);
		
		PorukaHelper.addSuccessAttribute(ra, "Cestitamo! Registracija je uspjesna.");
		
		return "redirect:/";
	}
}

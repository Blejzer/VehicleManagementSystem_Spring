package ba.fit.vms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;

@Controller
public class HomeController {
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(SecurityContextHolderAwareRequestWrapper principal, Model model) {
		if(principal != null)
		{
			if(principal.isUserInRole("ROLE_ADMIN")){
				String name = principal.getRemoteUser(); // kupimo logiranog korisnika
				Korisnik trenutni = korisnikRepository.readByEmail(name);
				model.addAttribute("user", trenutni.getIme() + " " + trenutni.getPrezime());
				return "admin/welcome";
			}else{
				if(principal.isUserInRole("ROLE_USER")){
					String name = principal.getRemoteUser(); // kupimo logiranog korisnika
					Korisnik trenutni = korisnikRepository.readByEmail(name);
					model.addAttribute("user", trenutni.getIme() + " " + trenutni.getPrezime());
					return "auth/welcome";
				}
			}
		}else{
			return "auth/index";
		}
		return "auth/index";
	}
	
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public String template() {
		return "../fragments/bootstrap-template";
	}

}

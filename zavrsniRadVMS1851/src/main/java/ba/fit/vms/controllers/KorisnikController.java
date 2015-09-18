package ba.fit.vms.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.SessionAttributes;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;

@Controller
@Secured("ROLE_USER")
@SessionAttributes("korisnikAtribut")
public class KorisnikController {
	
	private static final Logger LOG = LoggerFactory.getLogger(KorisnikController.class);
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@RequestMapping(value = "korisnik/current", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public Korisnik korisnici(UserDetails userDetails){
		LOG.info("Korisnicki podaci: "+userDetails.toString());
		return korisnikRepository.find(userDetails.getUsername());
	}

}

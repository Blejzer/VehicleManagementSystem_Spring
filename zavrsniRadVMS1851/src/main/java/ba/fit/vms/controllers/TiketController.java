package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Tiket;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.TiketRepository;
import ba.fit.vms.repository.VoziloRepository;

@Controller
public class TiketController {
	
	@Autowired
	private TiketRepository tiketRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private KorisnikVoziloRepository korisnikVoziloRepository;
	
	@RequestMapping(value="/korisnik/tiket/novi", method = RequestMethod.GET)
	public String getNoviTiket(HttpServletRequest request, Model model){
		String vin = request.getParameter("vin");
		Long id = Long.getLong(request.getParameter("id"));
		Tiket tiket = new Tiket();
		return "korisnik/tiket/novi";
	}

}

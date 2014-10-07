package ba.fit.vms.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.LokacijaKilometraza;
import ba.fit.vms.repository.KilometrazaRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;
import ba.fit.vms.repository.LokacijaRepository;

@Controller
public class LokacijaKilometrazaController {
	
	@Autowired
	private KilometrazaRepository kilometrazaRepository;
	
	@Autowired
	private LokacijaRepository lokacijaRepository;
	
	@Autowired
	private LokacijaKilometrazaRepository lkRepository;
	
	@Autowired
	private KorisnikVoziloRepository kvRepository;
	
	
	@RequestMapping(value="/admin/kilo/nova", method=RequestMethod.GET)
	public String getNovaKilometraza(Model model, HttpServletRequest request){
		
		Long id = Long.getLong(request.getParameter("id"));
		LokacijaKilometraza lk = new LokacijaKilometraza();
		lk.setKorisnikVozilo(kvRepository.findOne(id));
		
		model.addAttribute("lkAtribut", lk);
		
		return "/admin/vozila/kiLometraza/novi";
	}

}

package ba.fit.vms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.util.RegistracijaValidator;


@Controller
public class RegistracijaController {
	
	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;
	
	private RegistracijaValidator val = new RegistracijaValidator();
	
	
	
	@RequestMapping(value = "/admin/registrations/add", method = RequestMethod.GET)
	public String getAdd(@RequestParam(value="id", required=false) String vin, Model model) {

		if(vin==null){
			model.addAttribute("registrationAttribute", new Registracija());
			model.addAttribute("regVehicles", voziloRepository.getNeregistrovanaVozila());
		} else{
			model.addAttribute("registrationAttribute", new Registracija());
			model.addAttribute("regVehicles", voziloRepository.findByVin(vin));
		}

		return "/admin/registration/add";
	}

}

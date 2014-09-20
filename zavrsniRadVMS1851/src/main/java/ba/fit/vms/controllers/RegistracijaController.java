package ba.fit.vms.controllers;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.util.RegistracijaValidator;


@Controller
public class RegistracijaController {
	
	protected static Logger logger = Logger.getLogger("controller");
	
	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;
	
	private RegistracijaValidator val = new RegistracijaValidator();
	
	
	
	@RequestMapping(value = "/admin/registracija/novi", method = RequestMethod.GET)
	public String getAdd(@RequestParam(value="id", required=false) String vin, Model model) {

		if(vin==null){
			model.addAttribute("registracijaAtribut", new Registracija());
			model.addAttribute("regVehicles", voziloRepository.getNeregistrovanaVozila());
		} else{
			model.addAttribute("registracijaAtribut", new Registracija());
			model.addAttribute("regVehicles", voziloRepository.findByVin(vin));
		}

		return "/admin/vozila/registracija/novi";
	}
	
	
	@RequestMapping(value = "/admin/registracija/novi", method = RequestMethod.POST)
	public String postAdd(@ModelAttribute("registrationAttribute") @Valid Registracija registracija,
			BindingResult result) {
		logger.debug("poceo post");
		val.validate(registracija, result);
		logger.debug("uradio validaciju");
		Registracija reg = val.obrada(registracija, result);
		logger.debug("obradio");
		if(result.hasErrors()){
			return "/admin/registracija/novi?vin="+registracija.getVozilo().getVin();
		}
		registracijaRepository.save(reg);
		return "redirect:/admin/vozila/";
		
	}

}

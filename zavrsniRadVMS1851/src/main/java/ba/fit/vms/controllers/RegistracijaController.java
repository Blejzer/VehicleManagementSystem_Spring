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
	
	@Autowired
	private RegistracijaValidator val = new RegistracijaValidator();
	
	
	
	@RequestMapping(value = "/admin/registracija/novi", method = RequestMethod.GET)
	public String getAdd(@RequestParam(value="vin", required=false) String vin, Model model) {
		logger.debug("poceo get");
		logger.debug(registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		if(vin==null){
			model.addAttribute("registracijaAtribut", new Registracija());
			model.addAttribute("regVehicles", voziloRepository.getNeregistrovanaVozila());
			logger.debug("vin je null. pokupio neregistrovana vozila");
		} else{
			model.addAttribute("registracijaAtribut", new Registracija());
			model.addAttribute("regVehicles", voziloRepository.findOne(vin));
			logger.debug("Imamo vin, pokupio vozilo. " + model.toString());
		}

		return "/admin/vozila/registracija/novi";
	}
	
	
	@RequestMapping(value = "/admin/registracija/novi", method = RequestMethod.POST)
	public String postAdd(@ModelAttribute("registracijaAtribut") @Valid Registracija registracija,
			BindingResult result, Model model) {
		registracija.setVozilo(voziloRepository.findOne(registracija.getVozilo().getVin()));
		logger.debug("poceo post");
		val.validate(registracija, result);
		logger.debug("uradio validaciju");
		logger.debug("poceo obradu");
		Registracija reg = val.obrada(registracija, result);
		logger.debug("obradio");
		if(result.hasErrors()){
			model.addAttribute("regVehicles", registracija.getVozilo());
			return "/admin/vozila/registracija/novi";
		}
		registracijaRepository.save(reg);
		return "redirect:/admin/vozila/";
		
	}

}

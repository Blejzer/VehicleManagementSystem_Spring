package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Vozilo;
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
	
	@RequestMapping(value = "/admin/registracije/", method = RequestMethod.GET)
	public String list(HttpServletRequest request, Model model){
		logger.debug("Received request to show list page" + request.getParameter("vin"));
		Vozilo vozilo = voziloRepository.findOne(request.getParameter("vin"));
		logger.debug("vozilo: " + vozilo.getVin() + " found!");
		
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("vozilo", vozilo);
		model.addAttribute("pager", registracijaRepository.findAllByVozilo_Vin(vozilo.getVin(), pageable));
		return "/admin//vozila/registracija/lista";
	}
	
	@RequestMapping(value = "/admin/registracija/novi", method = RequestMethod.GET)
	public String getAdd(@RequestParam(value="vin", required=true) String vin, Model model) {
		logger.debug("poceo get");
		
		// logger.debug(registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		if(vin==null || vin.isEmpty()){
			model.addAttribute("registracijaAtribut", new Registracija());
			model.addAttribute("regVehicles", voziloRepository.getNeregistrovanaVozila());
			logger.debug("vin je null. pokupio neregistrovana vozila");
		} else{
			try {
				Registracija prethodna = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
				prethodna.setOsigOd(new DateTime(prethodna.getOsigOd()).plusYears(1).toDate());
				prethodna.setOsigDo(new DateTime(prethodna.getOsigDo()).plusYears(1).toDate());
				prethodna.setRegOd(new DateTime(prethodna.getRegOd()).plusYears(1).toDate());
				prethodna.setRegDo(new DateTime(prethodna.getRegDo()).plusYears(1).toDate());
				
				model.addAttribute("registracijaAtribut", prethodna);
				model.addAttribute("regVehicles", voziloRepository.findOne(vin));
				logger.debug("Imamo vin, pokupio vozilo. " + model.toString());
			} catch (Exception e) {
				Registracija nova = new Registracija();
				nova.setVozilo(voziloRepository.findOne(vin));
				model.addAttribute("registracijaAtribut", nova);
				model.addAttribute("regVehicles", voziloRepository.findOne(vin));
				
			}
			
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
		if(result.hasErrors()){
			model.addAttribute("regVehicles", registracija.getVozilo());
			return "/admin/vozila/registracija/novi";
		}
		logger.debug("poceo obradu");
		Registracija reg = val.obrada(registracija, result);
		logger.debug("obradio");
		if(result.hasErrors()){
			model.addAttribute("regVehicles", registracija.getVozilo());
			return "/admin/vozila/registracija/novi";
		}
		registracijaRepository.save(reg);
		return "redirect:/admin/vozila/?vin="+reg.getVozilo().getVin();
		
	}
	
	
	@RequestMapping(value = "/admin/registracije/izmjena", method = RequestMethod.GET)
	public String getIzmjenaRegistracije(@RequestParam(value="id", required=false) Long id, Model model) {
		logger.debug("poceo get");
		
		if(id==null){
			logger.debug("id je null. vracamo na listu registracija");
			return "/admin//vozila/registracija/lista";
		} else{
			Registracija registracija = registracijaRepository.findOne(id);
			logger.debug("nasao registraciju");
			model.addAttribute("registracijaAtribut", registracija);
			model.addAttribute("regVehicles", voziloRepository.findOne(registracija.getVozilo().getVin()));
			logger.debug("Imamo vin, pokupio vozilo. " + model.toString());
		}

		return "/admin/vozila/registracija/izmjena";
	}
	
	@RequestMapping(value = "/admin/registracije/izmjena", method = RequestMethod.POST)
	public String postIzmjenaRegistracije(@ModelAttribute("registracijaAtribut") @Valid Registracija registracija,
			BindingResult result, Model model) {
		registracija.setVozilo(voziloRepository.findOne(registracija.getVozilo().getVin()));
		logger.debug("poceo post");
		val.validate(registracija, result);
		logger.debug("uradio validaciju");
		if(result.hasErrors()){
			model.addAttribute("regVehicles", registracija.getVozilo());
			return "/admin/vozila/registracija/izmjena";
		}
		logger.debug("poceo obradu");
		Registracija reg = val.obrada(registracija, result);
		logger.debug("obradio");
		if(result.hasErrors()){
			model.addAttribute("regVehicles", registracija.getVozilo());
			return "/admin/vozila/registracija/izmjena";
		}
		registracijaRepository.save(reg);
		return "redirect:/admin/registracije/?vin="+reg.getVozilo().getVin();
		
	}

}

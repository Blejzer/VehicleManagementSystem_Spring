package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.LokacijaKilometraza;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;
import ba.fit.vms.repository.LokacijaRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.VoziloRepository;

@Controller
public class LokacijaKilometrazaController {
	
	@Autowired
	private LokacijaRepository lokacijaRepository;
	
	@Autowired
	private LokacijaKilometrazaRepository lkRepository;
	
	@Autowired
	private KorisnikVoziloRepository kvRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;

	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	
	@RequestMapping(value="/admin/kilo/nova", method=RequestMethod.GET)
	public String getNovaKilometraza(@RequestParam(value="vin", required=true) String vin, Model model, HttpServletRequest request){
		
		KorisnikVozilo kv = kvRepository.findByVozilo_VinAndVracenoNull(vin);
		if(kv==null){
			model.addAttribute("voziloAtribut", voziloRepository.findOne(vin));
			return "/admin/vozila/kvtest";
		}
		LokacijaKilometraza lk = new LokacijaKilometraza();
		lk.setKorisnikVozilo(kv);
		
		
		model.addAttribute("lkAtribut", lk);
		model.addAttribute("lAtribut", lokacijaRepository.findAll());
		
		return "/admin/vozila/kilometraza/novi";
	}
	
	
	@RequestMapping(value="/admin/kilo/nova", method = RequestMethod.POST)
	public String postNovaKilometraza(@ModelAttribute("lkAtribut") @Valid LokacijaKilometraza lk, BindingResult rezultat, Model model){
		if(rezultat.hasErrors()){
			model.addAttribute("lAtribut", lokacijaRepository.findAll());
			return "/admin/vozila/kilometraza/novi";
		}
		KorisnikVozilo kv = kvRepository.findOne(lk.getKorisnikVozilo().getId());
		lk.setKorisnikVozilo(kv);
		
		lkRepository.save(lk);
		return "redirect:/admin/kilo/lista"+kv.getVozilo().getVin();
	}
	
	@RequestMapping(value="/admin/kilo/lista", method=RequestMethod.GET)
	public String getListaKilometraze(@RequestParam(value="vin", required=true) String vin, Model model, HttpServletRequest request){
		if(vin.isEmpty() || vin==null){
			return "redirect:/admin/vozila/";
		}
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", lkRepository.findAll(pageable));
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		
		return "/admin/vozila/kilometraza/lista";
	}

}

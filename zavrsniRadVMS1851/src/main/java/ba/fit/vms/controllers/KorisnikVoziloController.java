package ba.fit.vms.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.log4j.Logger;
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

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.VoziloRepository;

@Controller
public class KorisnikVoziloController {

	protected static Logger logger = Logger.getLogger("controller");

	@Autowired
	private RegistracijaRepository registracijaRepository;

	@Autowired
	private KorisnikVoziloRepository kvRepository;

	@Autowired
	private KorisnikRepository korisnikRepository;

	@Autowired
	private VoziloRepository voziloRepository;



	@RequestMapping(value="/admin/dodjeljivanje/novi", method = RequestMethod.GET)
	public String getDodjelaVozila(@RequestParam Map<String,String> requestParams, Model model){
		String vin = requestParams.get("vin");
		logger.debug("vozilo: "+vin);
		if(kvRepository.findByVozilo_VinAndVracenoNull(vin)==null){
			logger.debug("trazim registraciju");
			Boolean regtest = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin)!=null;
			logger.debug(regtest);
			if(!regtest){
				return "redirect:/admin/registracija/novi?vin="+vin;
			}
			Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
			logger.debug("vozilo: "+reg.getTablica());
			KorisnikVozilo kv = new KorisnikVozilo();
			kv.setVozilo(reg.getVozilo());
			model.addAttribute("kvAtribut", kv);
			model.addAttribute("regAtribut", reg);
			model.addAttribute("korisnici", kvRepository.findAllUnassigned());
			model.addAttribute("prethodni", kvRepository.findAllByVozilo_Vin(vin));

		}else{
			Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
			model.addAttribute("regAtribut", reg);
			model.addAttribute("vozilo", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin).getVozilo());
			return ("redirect:/admin/dodjeljivanje/?vin="+vin);
		}
		return "/admin/assigning/novi";

	}
	
	
	@RequestMapping(value="/admin/dodjeljivanje/novi", method = RequestMethod.POST)
	public String postDodjelaVozila(@ModelAttribute("kvAtribut") @Valid KorisnikVozilo kv, BindingResult rezultat, Model model){
		
		if(rezultat.hasErrors()){
			model.addAttribute("korisnici", kvRepository.findAllUnassigned());
			model.addAttribute("regAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin()));
			model.addAttribute("prethodni", kvRepository.findAllByVozilo_Vin(kv.getVozilo().getVin()));
			return "/admin/assigning/novi";
		}
		logger.debug("nema gresaka, pokusavam snimiti dodjelu!");
		KorisnikVozilo kV = new KorisnikVozilo();
		Korisnik k = korisnikRepository.find(kv.getKorisnik().getEmail());
		Vozilo v = voziloRepository.findOne(kv.getVozilo().getVin());
		kV.setKorisnik(k);
		logger.debug("rijesio korisnika!");
		kV.setVozilo(v);
		kV.setDodijeljeno(kv.getDodijeljeno());
		kV.setVraceno(kv.getVraceno());
		kvRepository.save(kV);
		logger.debug("snimio dodjelu");
		return "redirect:/admin/dodjeljivanje/?vin="+kV.getVozilo().getVin();
		
	}
	
	@RequestMapping(value="/admin/dodjeljivanje/", method = RequestMethod.GET)
	public String getListaDodjela(HttpServletRequest request,  Model model){
		if(request.getParameter("vin")!=null){
			String vin = request.getParameter("vin");
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		KorisnikVozilo kv = kvRepository.findByVozilo_VinAndVracenoNull(vin);
		if(kv==null){
			kv = new KorisnikVozilo();
			kv.setVozilo(voziloRepository.findOne(vin));
		}
		model.addAttribute("trenutnoAtribut", kv);
		model.addAttribute("regAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		model.addAttribute("pager", kvRepository.findAllByVozilo_Vin(vin, pageable));
		return "/admin/assigning/lista";
		}else{
			return "redirect:/admin/vozila/";
		}
		
		
	}
	
	@RequestMapping(value="/admin/razduzi/", method=RequestMethod.GET)
	public String getRazduziVozilo(@RequestParam(value="id", required=true) Long id, Model model){
		
		KorisnikVozilo kv = kvRepository.findOne(id);
		String vin = kv.getVozilo().getVin();
		Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
		logger.debug("vozilo: "+reg.getTablica());
		model.addAttribute("kvAtribut", kv);
		model.addAttribute("regAtribut", reg);
		model.addAttribute("prethodni", kvRepository.findAllByVozilo_Vin(vin));
		return "/admin/assigning/izmjeni";
		
	}


}

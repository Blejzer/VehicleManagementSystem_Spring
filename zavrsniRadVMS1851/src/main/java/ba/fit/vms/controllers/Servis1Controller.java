package ba.fit.vms.controllers;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.LokacijaKilometraza;
import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Servis;
import ba.fit.vms.pojo.Servis1;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.DioRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;
import ba.fit.vms.repository.LokacijaRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.Servis1Repository;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.repository.VrstaServisaRepository;

@Controller
public class Servis1Controller {
	
	@Autowired
	private Servis1Repository servisRepository;
	
	@Autowired
	private KorisnikVoziloRepository kvRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;
	
	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	@Autowired
	private DioRepository dioRepository;
	
	@Autowired
	private VrstaServisaRepository vrstaServisaRepository;
	
	@Autowired
	private LokacijaRepository lokacijaRepository;
	
	@Autowired
	private LokacijaKilometrazaRepository lkRepository;
	
	
	/**
	 * Unos novog servisa
	 * @param vin
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/admin/servis/novi", method = RequestMethod.GET)
	public String getNoviServis(@RequestParam(value="vin", required=true) String vin, ModelMap map){
		if(vin==null || vin.isEmpty()){
			return "redirect:/admin/vozila/";
		}
		Vozilo v = voziloRepository.findOne(vin);
		KorisnikVozilo kv = new KorisnikVozilo();
		try {
			kv = kvRepository.findByVozilo_VinAndVracenoNull(vin);
		} catch (NoResultException e) {
			System.out.println("Vozilo nije dodijeljeno");
		}
		
		Registracija r = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
		Servis1 s = new Servis1();
		s.setVozilo(v);
		map.addAttribute("rAtribut", r);
		map.addAttribute("sAtribut", s);
		map.addAttribute("lAtribut", lokacijaRepository.findAll());
		map.addAttribute("dAtribut", dioRepository.findAll());
		map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
		
		return "admin/servis/servis/novi";	
	}
	
	/**
	 * Snimanje novog servisa
	 * @param servis
	 * @param rezultat
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/admin/servis/novi", method=RequestMethod.POST)
	public String postNoviServis(@ModelAttribute("sAtribut") @Valid Servis1 servis, BindingResult rezultat, ModelMap map){
		if(servis.getLokacijaKilometraza().getKilometraza()==null){ 
			rezultat.rejectValue("kilometraza.kilometraza", "kilometraza");
		}
		if(rezultat.hasErrors()){
			map.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(servis.getVozilo().getVin()));
			map.addAttribute("dAtribut", dioRepository.findAll());
			map.addAttribute("lAtribut", lokacijaRepository.findAll());
			map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
			System.out.println("greska");
			return "admin/servis/servis/novi";
		}
		servisRepository.save(servis);
		return "redirect:/admin/servis/?vin="+servis.getVozilo().getVin();
	}
	
	/**
	 * Izmjena postojeceg servisa
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/servis/izmjena", method = RequestMethod.GET)
	public String getIzmjenaServisa(@RequestParam(value="id", required=true) Long id, Model model){
		if(id==null){
			return "redirect:/admin/vozila/";
		}
		Servis1 s = servisRepository.findOne(id);
		
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(s.getVozilo().getVin()));
		model.addAttribute("dAtribut", dioRepository.findAll());
		model.addAttribute("lAtribut", lokacijaRepository.findAll());
		model.addAttribute("vAtribut", vrstaServisaRepository.findAll());
		model.addAttribute("sAtribut", s);
		
		return "/admin/servis/servis/izmjena";
		
	}
	
	/**
	 * Snimanje izmjene postojeceg servisa
	 * @param servis
	 * @param rezultat
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/admin/servis/izmjena", method=RequestMethod.POST)
	public String postIzmjenaServis(@ModelAttribute("sAtribut") @Valid Servis1 servis, BindingResult rezultat, ModelMap map){
		if(servis.getLokacijaKilometraza().getKilometraza()==null){
			rezultat.rejectValue("kilometraza.kilometraza", "kilometraza");
		}
		if(rezultat.hasErrors()){
			map.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(servis.getVozilo().getVin()));
			map.addAttribute("dAtribut", dioRepository.findAll());
			map.addAttribute("lAtribut", lokacijaRepository.findAll());
			map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
			System.out.println(rezultat.toString());
			return "admin/servis/servis/izmjena";
		}
		
		servisRepository.save(servis);
		return "redirect:/admin/servis/?vin="+servis.getVozilo().getVin();
	}
	
	
	/**
	 * Lista servisa po vozilu. Ukoliko nema vin, preusmjerava na pretragu servisa
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value={"/admin/servis/", "/admin/servis/lista"}, method = RequestMethod.GET)
	public String getSviServisiZaVozilo(Model model, HttpServletRequest request){
		String vin = request.getParameter("vin");
		if(vin.isEmpty() || vin==null){
			return "redirect:/admin/servis/pretraga";
		}
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", servisRepository.findAllByVozilo_Vin(vin, pageable));
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		return "/admin/servis/servis/lista";
	}

}

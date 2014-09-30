package ba.fit.vms.controllers;

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

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Servis;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.DioRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.ServisRepository;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.repository.VrstaServisaRepository;

@Controller
public class ServisController {
	
	@Autowired
	private ServisRepository servisRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;
	
	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	@Autowired
	private DioRepository dioRepository;
	
	@Autowired
	private VrstaServisaRepository vrstaServisaRepository;
	
	@RequestMapping(value={"/admin/servis/svi/", "/admin/servis/svi/lista"}, method = RequestMethod.GET)
	public String getSviServisi(Model model, HttpServletRequest request){

		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", servisRepository.findAll(pageable));
		return "/admin/servis/servis/listaSvih";
	}
	
	@RequestMapping(value="/admin/servis/zavrseni", method = RequestMethod.GET)
	public String getSviUradjeniServisi(Model model, HttpServletRequest request){

		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", servisRepository.findByZavrsenTrue(pageable));
		return "/admin/servis/dio/lista";
	}
	
	@RequestMapping(value="/admin/servis/novi", method = RequestMethod.GET)
	public String getNoviServis(@RequestParam(value="vin", required=true) String vin, ModelMap map){
		if(vin==null || vin.isEmpty()){
			return "redirect:/admin/vozila/";
		}
		Vozilo v = voziloRepository.findOne(vin);
		Registracija r = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
		Servis s = new Servis();
		s.setVozilo(v);
		map.addAttribute("rAtribut", r);
		map.addAttribute("sAtribut", s);
		map.addAttribute("dAtribut", dioRepository.findAll());
		map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
		
		return "admin/servis/servis/novi";	
	}
	
	@RequestMapping(value="/admin/servis/novi", method=RequestMethod.POST)
	public String postNoviServis(@ModelAttribute("sAtribut") @Valid Servis servis, BindingResult rezultat, ModelMap map){
		if(servis.getKilometraza().getKilometraza()==null || servis.getKilometraza().getDatum()==null){
			rezultat.rejectValue("kilometraza.datum", "datum");
			rezultat.rejectValue("kilometraza.kilometraza", "kilometraza");
		}
		if(rezultat.hasErrors()){
			map.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(servis.getVozilo().getVin()));
			map.addAttribute("dAtribut", dioRepository.findAll());
			map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
			return "admin/servis/servis/novi";
		}
		
		servisRepository.save(servis);
		return "admin/servis/servis/lista?vin="+servis.getVozilo().getVin();
	}
	
	@RequestMapping(value={"/admin/servis/", "/admin/servis/lista"}, method = RequestMethod.GET)
	public String getSviServisiZaVozilo(Model model, HttpServletRequest request){
		String vin = request.getParameter("vin");
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
		model.addAttribute("pager", servisRepository.findAllByVozilo_Vin(vin, pageable));
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		return "/admin/servis/servis/lista";
	}

}

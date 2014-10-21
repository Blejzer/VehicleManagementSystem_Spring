package ba.fit.vms.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import ba.fit.vms.pojo.StariServis;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.DioRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.StariServisRepository;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.repository.VrstaServisaRepository;
import ba.fit.vms.util.ServisPretraga;


public class StariServisController {
	
	@Autowired
	private StariServisRepository stariServisRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;
	
	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	@Autowired
	private DioRepository dioRepository;
	
	@Autowired
	private VrstaServisaRepository vrstaServisaRepository;

	
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
		Registracija r = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
		StariServis s = new StariServis();
		s.setVozilo(v);
		map.addAttribute("rAtribut", r);
		map.addAttribute("sAtribut", s);
		map.addAttribute("dAtribut", dioRepository.findAll());
		map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
		
		return "admin/servis/servis/novi";	
	}
	
	
	/**
	 * Snimanje novog servisa
	 * @param stariServis
	 * @param rezultat
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/admin/servis/novi", method=RequestMethod.POST)
	public String postNoviServis(@ModelAttribute("sAtribut") @Valid StariServis stariServis, BindingResult rezultat, ModelMap map){
		if(false){ //servis.getKilometraza().getKilometraza()==null
			rezultat.rejectValue("kilometraza.kilometraza", "kilometraza");
		}
		if(rezultat.hasErrors()){
			map.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(stariServis.getVozilo().getVin()));
			map.addAttribute("dAtribut", dioRepository.findAll());
			map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
			return "admin/servis/servis/novi";
		}
		
		stariServisRepository.save(stariServis);
		return "redirect:/admin/servis/?vin="+stariServis.getVozilo().getVin();
	}
	
	@RequestMapping(value="/admin/servis/izmjena", method = RequestMethod.GET)
	public String getIzmjenaServisa(@RequestParam(value="id", required=true) Long id, Model model){
		if(id==null){
			return "redirect:/admin/vozila/";
		}
		StariServis s = stariServisRepository.findOne(id);
		
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(s.getVozilo().getVin()));
		model.addAttribute("dAtribut", dioRepository.findAll());
		model.addAttribute("vAtribut", vrstaServisaRepository.findAll());
		model.addAttribute("sAtribut", s);
		
		return "/admin/servis/servis/izmjena";
		
	}
	
	@RequestMapping(value="/admin/servis/izmjena", method=RequestMethod.POST)
	public String postIzmjenaServis(@ModelAttribute("sAtribut") @Valid StariServis stariServis, BindingResult rezultat, ModelMap map){
		if(false){ //servis.getKilometraza().getKilometraza()==null
			rezultat.rejectValue("kilometraza.kilometraza", "kilometraza");
		}
		if(rezultat.hasErrors()){
			map.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(stariServis.getVozilo().getVin()));
			map.addAttribute("dAtribut", dioRepository.findAll());
			map.addAttribute("vAtribut", vrstaServisaRepository.findAll());
			System.out.println(rezultat.toString());
			return "admin/servis/servis/izmjena";
		}
		
		stariServisRepository.save(stariServis);
		return "redirect:/admin/servis/?vin="+stariServis.getVozilo().getVin();
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
		model.addAttribute("pager", stariServisRepository.findAllByVozilo_Vin(vin, pageable));
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		return "/admin/servis/servis/lista";
	}
	
	
	/**
	 * Pretraga servisa get metoda otvara formu za pretragu
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/admin/servis/pretraga", method = RequestMethod.GET)
	public String listCustomServis(HttpServletRequest request, HttpServletResponse response, Model model){
		ServisPretraga search = new ServisPretraga(registracijaRepository);
		Boolean first = true;
		model.addAttribute("searchAttribute", search);
		model.addAttribute("first", first);
		return "/admin/servis/servis/listaSvih";
	}
	
	@RequestMapping(value = "/admin/servis/pretraga", method = RequestMethod.POST)
	public String findCustomServis(	@ModelAttribute("searchAttribute") ServisPretraga search,
			Model model){

		ServisPretraga searchnew = new ServisPretraga(registracijaRepository);
		searchnew.setVin(search.getVin());
		searchnew.setMjesec(search.getMjesec());
		searchnew.setGodina(search.getGodina());
		model.addAttribute("searchAttribute", searchnew);
		LinkedHashMap<Registracija, List<StariServis>> report = new LinkedHashMap<Registracija, List<StariServis>>();

		if (!search.getVin().isEmpty()) {
			List<StariServis> servisi = new ArrayList<StariServis>(stariServisRepository.getCustomServis(searchnew.getVin(), Integer.parseInt(searchnew.getGodina().toString()), Integer.parseInt(searchnew.getMjesec().toString())));
			if (!servisi.isEmpty()) {
				report.put(registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(searchnew.getVin()), servisi);
			} else {
				model.addAttribute("vehicleVin", search.getVin());
			}
		} else {
			for (Registracija registracija : searchnew.getRegistracije()) {
				List<StariServis> servisi = new ArrayList<StariServis>(stariServisRepository.getCustomServis(registracija.getVozilo().getVin(), Integer.parseInt(searchnew.getGodina().toString()), Integer.parseInt(searchnew.getMjesec().toString())));
				if (!servisi.isEmpty()) {
					report.put(registracija, servisi);
				} else {
					//report.put(vehicle.getVin(),null);
				}
			}
			//reporti.put(LP, report);
		}

		model.addAttribute("report", report);
		//model.addAttribute("reporti", reporti);

		return "/admin/servis/servis/listaSvih";
	}

}

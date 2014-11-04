package ba.fit.vms.controllers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

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
import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;
import ba.fit.vms.repository.LokacijaRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.util.KilometrazaPretraga;
import ba.fit.vms.util.LokacijaKilometrazaValidator;

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
	
	@Autowired
	private LokacijaKilometrazaValidator validator = new LokacijaKilometrazaValidator();
	
	
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
		lk.setKorisnikVozilo(kvRepository.findOne(lk.getKorisnikVozilo().getId()));
		System.out.println("pozivam validator" + lk.getKorisnikVozilo().getVozilo().getVin());
		validator.validate(lk, rezultat);
		System.out.println("uradio validaciju");
		if(rezultat.hasErrors()){
			model.addAttribute("lAtribut", lokacijaRepository.findAll());
			return "/admin/vozila/kilometraza/novi";
		}
		lkRepository.save(lk);
		Vozilo v = voziloRepository.findOne(lk.getKorisnikVozilo().getVozilo().getVin());
		return "redirect:/admin/kilo/lista?vin="+v.getVin();
	}
	
	@RequestMapping(value="/admin/kilo/lista", method=RequestMethod.GET)
	public String getListaKilometraze( Model model, HttpServletRequest request){
		String vin = request.getParameter("vin");
		
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
			if(vin.isEmpty() || vin==null){
				return "redirect:/admin/vozila/";
			}
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", lkRepository.findByKorisnikVozilo_VoziloVinOrderByKilometrazaDesc(vin, pageable));
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin));
		
		return "/admin/vozila/kilometraza/lista";
	}
	
	@RequestMapping(value="/admin/kilo/pregled", method=RequestMethod.GET)
	public String getKilometrazaPregled(Model model){
		KilometrazaPretraga pretraga = new KilometrazaPretraga(voziloRepository);
		Boolean first = true;
		model.addAttribute("kpAtribut", pretraga);
		model.addAttribute("first", first);
		return "/admin/vozila/kilometraza/pregled";
	}
	
	@RequestMapping(value="/admin/kilo/pregled", method=RequestMethod.POST)
	public String postKilometrazaPregled(@ModelAttribute("kpAtribut") KilometrazaPretraga pretraga, Model model){
		
		// Podesavamo pretragu da pamti prethodnu
		KilometrazaPretraga nova = new KilometrazaPretraga(voziloRepository);
		if (!pretraga.getVin().isEmpty()) {
			nova.setVin(pretraga.getVin());
		}
		nova.setMjesec(pretraga.getMjesec());
		nova.setGodina(pretraga.getGodina());
		Boolean first = false;
		model.addAttribute("first", first);
		model.addAttribute("kpAtribut", nova);
		System.out.println("vozilo="+pretraga.getVin()+!pretraga.getVin().isEmpty()+", godina= "+pretraga.getGodina()+", mjesec= "+pretraga.getMjesec());
		//////////////////////////////////////
		
		LinkedHashMap<Registracija, List<LokacijaKilometraza>> pregled = new LinkedHashMap<Registracija, List<LokacijaKilometraza>>();
		
		if(!pretraga.getVin().isEmpty()){ // Odabrano je vozilo
			List<LokacijaKilometraza> kilometraze = new ArrayList<LokacijaKilometraza>(lkRepository.pregled(pretraga.getVin(), pretraga.getGodina(), pretraga.getMjesec()));
			if (!kilometraze.isEmpty()) {
				Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(pretraga.getVin());
				pregled.put(reg, kilometraze);				
			} else {
				System.out.println("prazna lista");
			}
		} else {
			for (Vozilo vozilo : nova.getVozila()) { // za sva vozila koja su nekad dodijeljena i imaju unesenu kilometrazu
				
				List<LokacijaKilometraza> kilometraze = new ArrayList<LokacijaKilometraza>(lkRepository.pregled(vozilo.getVin(), pretraga.getGodina(), pretraga.getMjesec()));
				if (!kilometraze.isEmpty()) {
					Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vozilo.getVin());
					pregled.put(reg, kilometraze);		
				} else {
					System.out.println("prazna lista");
				}
				
			}
		}
		model.addAttribute("pregledAtribut", pregled);
		return "/admin/vozila/kilometraza/pregled";
		
	}
	
	
	@RequestMapping(value="/admin/kilo/izvjestaj", method=RequestMethod.GET)
	public String getKilometrazaIzvjestaj(Model model){
		KilometrazaPretraga pretraga = new KilometrazaPretraga(voziloRepository);
		Boolean first = true;
		model.addAttribute("kpAtribut", pretraga);
		model.addAttribute("first", first);
		return "/admin/izvjestaji/kilometraza";
	}
	
	@RequestMapping(value="/admin/kilo/izvjestaj", method=RequestMethod.POST)
	public String postKilometrazaIzvjestaj(@ModelAttribute("kpAtribut") KilometrazaPretraga pretraga, Model model){
		
		// Podesavamo pretragu da pamti prethodnu
		KilometrazaPretraga nova = new KilometrazaPretraga(voziloRepository);
		if (!pretraga.getVin().isEmpty()) {
			nova.setVin(pretraga.getVin());
		}
		nova.setMjesec(pretraga.getMjesec());
		nova.setGodina(pretraga.getGodina());
		Boolean first = false;
		model.addAttribute("first", first);
		model.addAttribute("kpAtribut", nova);
		System.out.println("vozilo="+pretraga.getVin()+!pretraga.getVin().isEmpty()+", godina= "+pretraga.getGodina()+", mjesec= "+pretraga.getMjesec());
		//////////////////////////////////////
		
		LinkedHashMap<Registracija, List<LokacijaKilometraza>> pregled = new LinkedHashMap<Registracija, List<LokacijaKilometraza>>();
		List<LokacijaKilometraza> zadnja = new ArrayList<LokacijaKilometraza>();
		LokacijaKilometraza test = new LokacijaKilometraza();
		if(!pretraga.getVin().isEmpty()){ // Odabrano je vozilo
			List<LokacijaKilometraza> kilometraze = new ArrayList<LokacijaKilometraza>(lkRepository.pregled(pretraga.getVin(), pretraga.getGodina(), pretraga.getMjesec()));
			if (!kilometraze.isEmpty()) {
				Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(pretraga.getVin());
				pregled.put(reg, kilometraze);				
			} else {
				System.out.println("prazna lista");
			}
		} else {
			for (Vozilo vozilo : nova.getVozila()) { // za sva vozila koja su nekad dodijeljena i imaju unesenu kilometrazu
				
				List<LokacijaKilometraza> kilometraze = new ArrayList<LokacijaKilometraza>(lkRepository.pregled(vozilo.getVin(), pretraga.getGodina(), pretraga.getMjesec()));
				if (!kilometraze.isEmpty()) {
					Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vozilo.getVin());
					pregled.put(reg, kilometraze);
					test = lkRepository.getMaxMileagePrevious(vozilo.getVin(), pretraga.getMjesec()-1, pretraga.getGodina());
					if(test==null){
						test = lkRepository.getMinMileagePrevious(vozilo.getVin(), pretraga.getMjesec(), pretraga.getGodina());
					}
					zadnja.add(test);
					
				} else {
					System.out.println("prazna lista");
				}
				
			}
		}
		model.addAttribute("pregledAtribut", pregled);
		model.addAttribute("zAtribut", zadnja);
		return "/admin/izvjestaji/kilometraza";
		
	}

}

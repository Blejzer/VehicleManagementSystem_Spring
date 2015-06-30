package ba.fit.vms.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
		KorisnikVozilo kv = new KorisnikVozilo();
		try {
			kv = kvRepository.findByVozilo_VinAndVracenoNull(vin);
			System.out.println("kv uradjen "+ kv.getId());
		} catch (Exception e) {
			model.addAttribute("voziloAtribut", voziloRepository.findOne(vin));
			System.out.println(e.getMessage());
			return "/admin/vozila/kvtest";
		}
		
		LokacijaKilometraza lk = new LokacijaKilometraza();
		lk.setDatum(new Date());
		try {
			lk.setKilometraza(lkRepository.getMaxMileage(vin).getKilometraza());
		} catch (Exception e) {
			System.out.println("greska: "+e.getMessage());
		}
		
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
		KilometrazaPretraga pretraga = new KilometrazaPretraga(registracijaRepository);
		Boolean first = true;
		model.addAttribute("kpAtribut", pretraga);
		model.addAttribute("first", first);
		System.out.println(pretraga.getRegistracije().size());
		return "/admin/vozila/kilometraza/pregled";
	}
	
	@RequestMapping(value="/admin/kilo/pregled", method=RequestMethod.POST)
	public String postKilometrazaPregled(@ModelAttribute("kpAtribut") KilometrazaPretraga pretraga, Model model){
		
		// Podesavamo pretragu da pamti prethodnu
		KilometrazaPretraga nova = new KilometrazaPretraga(registracijaRepository);
		if (!pretraga.getVin().isEmpty()) {
			nova.setVin(pretraga.getVin());
		}
		nova.setMjesec(pretraga.getMjesec());
		nova.setGodina(pretraga.getGodina());
		Boolean first = false;
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
			for (Registracija registracija : nova.getRegistracije()) { // za sva vozila koja su nekad dodijeljena i imaju unesenu kilometrazu
				
				List<LokacijaKilometraza> kilometraze = new ArrayList<LokacijaKilometraza>(lkRepository.pregled(registracija.getVozilo().getVin(), pretraga.getGodina(), pretraga.getMjesec()));
				if (!kilometraze.isEmpty()) {
					// Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vozilo.getVin());
					pregled.put(registracija, kilometraze);		
				} else {
					System.out.println("prazna lista");
				}
				
			}
		}
		model.addAttribute("first", first);
		model.addAttribute("kpAtribut", nova);
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
		
		System.out.println("Godina= "+pretraga.getGodina()+", Mjesec= "+pretraga.getMjesec());
		//////////////////////////////////////
		
		Date lastDayOfThisMonth = new DateTime().withMonthOfYear(pretraga.getMjesec()).withYear(pretraga.getGodina()).dayOfMonth().withMaximumValue().toDate();
		Date lastDayOfPreviousMonth = new DateTime(lastDayOfThisMonth).minusMonths(1).dayOfMonth().withMaximumValue().toDate();
		
		LinkedHashMap<Registracija, List<LokacijaKilometraza>> pregled = new LinkedHashMap<Registracija, List<LokacijaKilometraza>>();
		for (Registracija regVozilo : registracijaRepository.findAllByJeAktivnoTrueOrderByRegDoDesc()) {
			try {
				List<LokacijaKilometraza> vozila = new ArrayList<LokacijaKilometraza>();
				LokacijaKilometraza temp = new LokacijaKilometraza();
				temp = lkRepository.getMaxKilo1(regVozilo.getVozilo().getVin(), lastDayOfPreviousMonth, lastDayOfThisMonth);
				Integer k = 0;
				k = temp.getKilometraza().intValue();
				vozila.add(temp);
				
				try {
					temp = lkRepository.getMaxKilo2(regVozilo.getVozilo().getVin(), lastDayOfPreviousMonth);
					temp.setKilometraza(Long.valueOf(k-temp.getKilometraza().intValue()));
					vozila.add(temp);
				} catch (Exception e) {
					temp.setKilometraza(Long.valueOf(k));
					vozila.add(temp);
				}
				if(vozila.size()>1){
					pregled.put(regVozilo, vozila);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		
		/*LinkedHashMap<Registracija, List<LokacijaKilometraza>> pregled = new LinkedHashMap<Registracija, List<LokacijaKilometraza>>();
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
		}*/
		model.addAttribute("first", first);
		model.addAttribute("kpAtribut", nova);
		model.addAttribute("pregledAtribut", pregled);
		// model.addAttribute("zAtribut", zadnja);
		return "/admin/izvjestaji/kilometraza";
		
	}
	
	@RequestMapping(value="/admin/kilo/izmjena", method=RequestMethod.GET)
	public String getIzmjenaKilometraza(@RequestParam(value="id", required=true) Long id, Model model, HttpServletRequest request){
		System.out.println("id je: "+id);
		LokacijaKilometraza lk = new LokacijaKilometraza();
		try {
			lk = lkRepository.findOne(id);
			System.out.println(lk.getId());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		model.addAttribute("lkAtribut", lk);
		model.addAttribute("lAtribut", lokacijaRepository.findAll());
		
		return "/admin/vozila/kilometraza/izmjena";
	}
	
	
	@RequestMapping(value="/admin/kilo/izmjena", method = RequestMethod.POST)
	public String postIzmjenaKilometraza(@ModelAttribute("lkAtribut") @Valid LokacijaKilometraza lk, BindingResult rezultat, Model model){
		
		lk.setKorisnikVozilo(kvRepository.findOne(lk.getKorisnikVozilo().getId()));
		System.out.println("pozivam validator za vin: " + lk.getKorisnikVozilo().getVozilo().getVin());
		
		validator.validate(lk, rezultat);
		System.out.println("uradio validaciju. Broj gresaka: " + rezultat.getErrorCount());
		System.out.println("Greske: " + rezultat.getAllErrors());
		
		if(rezultat.hasErrors()){
			model.addAttribute("lkAtribut", lkRepository.findOne(lk.getId()));
			model.addAttribute("lAtribut", lokacijaRepository.findAll());
			return "/admin/vozila/kilometraza/izmjena";
		}
		
		LokacijaKilometraza lkNew = new LokacijaKilometraza();
		lkNew.setId(lk.getId());
		lkNew.setDatum(lk.getDatum());
		lkNew.setKilometraza(lk.getKilometraza());
		lkNew.setKorisnikVozilo(kvRepository.findOne(lk.getKorisnikVozilo().getId()));
		lkNew.setLokacija(lokacijaRepository.findOne(lk.getLokacija().getId()));
		//lkNew.getLokacija().setNaziv(lokacijaRepository.findOne(lk.getLokacija().getId()).getNaziv());
		System.out.println("Lokacija: " + lkNew.getLokacija().getNaziv());
		lkRepository.saveAndFlush(lkNew);
		Vozilo v = voziloRepository.findOne(lk.getKorisnikVozilo().getVozilo().getVin());
		return "redirect:/admin/kilo/lista?vin="+v.getVin();
	}
	
	@RequestMapping(value="/admin/kilo/izbrisi", method=RequestMethod.GET)
	public String getIzbrisiKilometraza(@RequestParam(value="id", required=true) Long id, Model model, HttpServletRequest request){
		LokacijaKilometraza lk = new LokacijaKilometraza();
		try {
			lk = lkRepository.findOne(id);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		lkRepository.delete(lk);
		
		return "redirect:/admin/kilo/lista?vin="+lk.getKorisnikVozilo().getVozilo().getVin();
	}
	

}

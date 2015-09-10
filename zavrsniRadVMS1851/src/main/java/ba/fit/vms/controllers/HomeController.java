package ba.fit.vms.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.LokacijaKilometraza;
import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Servis1;
import ba.fit.vms.pojo.Tiket2;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.Servis1Repository;
import ba.fit.vms.repository.Tiket2Repository;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.util.ServisPretraga;

@Controller
public class HomeController {
	
	protected static Logger logger = Logger.getLogger("repo");
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private KorisnikVoziloRepository korisnikVoziloRepository;
	
	@Autowired
	private VoziloRepository vRepository;
	@Autowired
	private RegistracijaRepository regRepository;
	
	@Autowired
	private Servis1Repository servisRepository;
	
	@Autowired
	private LokacijaKilometrazaRepository lokiRepository;
	
	@Autowired
	private Tiket2Repository tRepository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(SecurityContextHolderAwareRequestWrapper principal, Model model) {
		List<Boolean> atributi=new ArrayList<Boolean>(Arrays.asList(new Boolean[10]));
		Collections.fill(atributi, Boolean.FALSE);
		
		
		if(principal != null)
		{
			if(principal.isUserInRole("ROLE_ADMIN")){
				String name = principal.getRemoteUser(); // kupimo logiranog korisnika
				Korisnik trenutni = korisnikRepository.find(name);
				
				// DODIJELJENA VOZILA TAB
				TreeMap<Registracija, KorisnikVozilo> pregled2 = new TreeMap<Registracija, KorisnikVozilo>(); // Ovdje koristimo TreeMap uz implementiran Complerable interfejs u objektu Registracija
				Registracija reg = new Registracija();
				List<KorisnikVozilo> dodijeljeni = new ArrayList<KorisnikVozilo>();
				dodijeljeni = korisnikVoziloRepository.findByVracenoNull();
				for (KorisnikVozilo kovo : dodijeljeni) {
					reg = regRepository.findByVozilo_VinAndJeAktivnoTrue(kovo.getVozilo().getVin());
					pregled2.put(reg, kovo);
				}
				// KRAJ DODIJELJENA VOZILA TAB
				
				// SLOBODNA VOZILA TAB
				List<Registracija> slobReg = new ArrayList<Registracija>();
				List<Vozilo> slobodna = new ArrayList<Vozilo>();
				slobodna = korisnikVoziloRepository.findAllUnassignedV();
				for (Vozilo vozilo : slobodna) {
					try {
						slobReg.add(regRepository.findByVozilo_VinAndJeAktivnoTrue(vozilo.getVin()));
					} catch (Exception e) {
						System.out.println("poruka je: " + e.getMessage());
					}
					
				}				
				// KRAJ SLOBODNA VOZILA TAB
				
				// REGISTRACIJA TAB
				Date d1 = new DateTime().plusMonths(1).toDate();
				
				List<Registracija> registracije = regRepository.findAllByJeAktivnoTrueOrderByRegDoDesc();
				List<Registracija> lista = new ArrayList<Registracija>();
				for (Registracija registracija : registracije) {
					if(registracija.getOsigDo().before(d1) || registracija.getRegDo().before(d1)){
						lista.add(registracija);
					}
				}
				if (lista.size()>0) {
					atributi.set(1, true);
				}
				
				List<Vozilo> listaVozila = vRepository.getNeregistrovanaVozila();
				System.out.println("Broj neregistrovanih vozila: "+listaVozila.size());
				
				// KRAJ REGISTRACIJA TAB
				
				// SERVISI TAB
				Date lastDayOfThisMonth = new DateTime().dayOfMonth().withMaximumValue().toDate();
				Date lastDayOfPreviousMonth = new DateTime().minusMonths(1).dayOfMonth().withMaximumValue().toDate();
				ServisPretraga searchnew = new ServisPretraga(regRepository);
				LinkedHashMap<Registracija, List<Servis1>> report = new LinkedHashMap<Registracija, List<Servis1>>();
				for (Registracija registracija : searchnew.getRegistracije()) {
					List<Servis1> servisi = new ArrayList<Servis1>(servisRepository.findByVozilo_vinAndDatumBetweenOrderByDatumAsc(registracija.getVozilo().getVin(), lastDayOfPreviousMonth, lastDayOfThisMonth));
					if (!servisi.isEmpty()) {
						report.put(registracija, servisi);
						for (Servis1 servis1 : servisi) {
							if(!servis1.getZavrsen()){
								atributi.set(3, true);
							}
						}	
					}
				}				
				// KRAJ SERVISI TAB
				
				// KILOMETRAZA TAB
				
				
				LinkedHashMap<Registracija, List<LokacijaKilometraza>> kilometraza = new LinkedHashMap<Registracija, List<LokacijaKilometraza>>();
				for (Registracija regVozilo : regRepository.findAllByJeAktivnoTrueOrderByRegDoDesc()) {
					try {
						List<LokacijaKilometraza> vozila = new ArrayList<LokacijaKilometraza>();
						LokacijaKilometraza temp = new LokacijaKilometraza();
						temp = lokiRepository.getMaxKilo1(regVozilo.getVozilo().getVin(), lastDayOfPreviousMonth, lastDayOfThisMonth);
						Integer k = 0;
						k = temp.getKilometraza().intValue();
						vozila.add(temp);
						
						try {
							temp = lokiRepository.getMaxKilo2(regVozilo.getVozilo().getVin(), lastDayOfPreviousMonth);
							temp.setKilometraza(Long.valueOf(k-temp.getKilometraza().intValue()));
							vozila.add(temp);
						} catch (Exception e) {
							temp.setKilometraza(Long.valueOf(k));
							vozila.add(temp);
						}
						if(vozila.size()>1){
							kilometraza.put(regVozilo, vozila);
						}
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
				}
				// KRAJ KILOMETRAZA TAB
				
				// TIKET TAB
				
				int page=0;
				int pageSize = 4;
				Pageable pageable = new PageRequest(page, pageSize);
				try {
					Page<Tiket2> pages = tRepository.findByRijesenDatumIsNullOrderByTiketDatumDesc(pageable);
					model.addAttribute("tAtribut", pages);
					atributi.set(5, true);
				} catch (Exception e) {
					System.out.println("Poruka o gresci: "+e.getMessage());
				}
				
				model.addAttribute("user", trenutni.getIme() + " " + trenutni.getPrezime());
				model.addAttribute("regAtribut", lista);
				model.addAttribute("nerRegAtribut", listaVozila);
				model.addAttribute("report", report);
				model.addAttribute("dodAtribut", pregled2);
				model.addAttribute("slobAtribut", slobReg);
				model.addAttribute("mileageAtribut", kilometraza);
				model.addAttribute("boolAtributi", atributi);
				return "admin/welcome";
			}else{
				if(principal.isUserInRole("ROLE_USER")){
					String name = principal.getRemoteUser(); // kupimo logiranog korisnika
					Korisnik trenutni = korisnikRepository.find(name);
					KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(name);
					model.addAttribute("user", trenutni.getIme() + " " + trenutni.getPrezime());
					model.addAttribute("kvAtribut", kv);
					return "auth/welcome";
				}
			}
		}else{
			return "auth/index";
		}
		return "auth/index";
	}
	
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public String template() {
		return "dodaci/bootstrap-template";
	}

}

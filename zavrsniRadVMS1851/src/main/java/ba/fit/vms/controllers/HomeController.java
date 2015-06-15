package ba.fit.vms.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Servis1;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.Servis1Repository;
import ba.fit.vms.util.ServisPretraga;

@Controller
public class HomeController {
	
	protected static Logger logger = Logger.getLogger("repo");
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private KorisnikVoziloRepository korisnikVoziloRepository;
	
	@Autowired
	private RegistracijaRepository regRepository;
	
	@Autowired
	private Servis1Repository servisRepository;
	
	@Autowired
	private LokacijaKilometrazaRepository lokiRepository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(SecurityContextHolderAwareRequestWrapper principal, Model model) {
		List<Boolean> atributi=new ArrayList<Boolean>(Arrays.asList(new Boolean[10]));
		Collections.fill(atributi, Boolean.FALSE);
		Date d = new DateTime().minusMonths(1).toDate();
		if(principal != null)
		{
			if(principal.isUserInRole("ROLE_ADMIN")){
				String name = principal.getRemoteUser(); // kupimo logiranog korisnika
				Korisnik trenutni = korisnikRepository.find(name);
				
				// DODIJELJENA VOZILA TAB
				HashMap<Registracija, KorisnikVozilo> pregled = new LinkedHashMap<Registracija, KorisnikVozilo>();
				Registracija reg = new Registracija();
				List<KorisnikVozilo> dodijeljeni = new ArrayList<KorisnikVozilo>();
				dodijeljeni = korisnikVoziloRepository.findByVracenoNull();
				for (KorisnikVozilo kovo : dodijeljeni) {
					reg = regRepository.findByVozilo_VinAndJeAktivnoTrue(kovo.getVozilo().getVin());
					pregled.put(reg, kovo);
				}
				// KRAJ DODIJELJENA VOZILA TAB
				
				// SLOBODNA VOZILA TAB
				List<Registracija> slobReg = new ArrayList<Registracija>();
				List<Vozilo> slobodna = new ArrayList<Vozilo>();
				slobodna = korisnikVoziloRepository.findAllUnassignedV();
				for (Vozilo vozilo : slobodna) {
					slobReg.add(regRepository.findByVozilo_VinAndJeAktivnoTrue(vozilo.getVin()));
				}				
				// KRAJ SLOBODNA VOZILA TAB
				
				// REGISTRACIJA TAB
				List<Registracija> registracije = regRepository.findAllByJeAktivnoTrue();
				
				for (Registracija registracija : registracije) {
					
					if(registracija.getOsigDo().after(d) || registracija.getRegDo().after(d)){
						atributi.set(1, true);
					}
				}
				// KRAJ REGISTRACIJA TAB
				
				// SERVISI TAB
				ServisPretraga searchnew = new ServisPretraga(regRepository);
				LinkedHashMap<Registracija, List<Servis1>> report = new LinkedHashMap<Registracija, List<Servis1>>();
				for (Registracija registracija : searchnew.getRegistracije()) {
					List<Servis1> servisi = new ArrayList<Servis1>(servisRepository.getCustomServis(registracija.getVozilo().getVin(), Integer.parseInt(searchnew.getGodina().toString()), Integer.parseInt(searchnew.getMjesec().toString())));
					if (!servisi.isEmpty()) {
						report.put(registracija, servisi);
						for (Servis1 servis1 : servisi) {
							if(servis1.getDatum().after(d)){
								atributi.set(3, true);	
							}
						}
					}
				}				
				// KRAJ SERVISI TAB
				
				// KILOMETRAZA TAB
				
				
				
				// KRAJ KILOMETRAZA TAB
				
				
				model.addAttribute("user", trenutni.getIme() + " " + trenutni.getPrezime());
				model.addAttribute("regAtribut", reg);
				model.addAttribute("report", report);
				model.addAttribute("dodAtribut", pregled);
				model.addAttribute("slobAtribut", slobReg);
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

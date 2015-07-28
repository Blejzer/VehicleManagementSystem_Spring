package ba.fit.vms.controllers;

import java.security.Principal;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Tiket2;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.Tiket2Repository;
import ba.fit.vms.repository.VoziloRepository;

@Controller
public class Tiket2Controller {
	
	@Autowired
	private Tiket2Repository tiket2Repository;
	
	@Autowired
	private VoziloRepository vRepository;
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private RegistracijaRepository rRepository;
	
	@Autowired
	private KorisnikVoziloRepository kvRepository;
	
	@RequestMapping(value="/korisnik/tiketi/novi", method = RequestMethod.GET)
	public String getNoviTiket(Principal principal, Model model){
		String email = principal.getName();
		System.out.println(email);
		KorisnikVozilo kvTemp = new KorisnikVozilo();
		Tiket2 tiket2 = new Tiket2();
		try {
			kvTemp = kvRepository.findByKorisnik_EmailAndVracenoNull(email);
			tiket2.setKorisnik(kvTemp.getKorisnik());
			tiket2.setVozilo(vRepository.findOne(kvTemp.getVozilo().getVin()));
			model.addAttribute("rAtribut", rRepository.findByVozilo_VinAndJeAktivnoTrue(kvTemp.getVozilo().getVin()));
			System.out.println("dodao korisnika: "+kvTemp.getKorisnik().getEmail());
		} catch (Exception e) {
			tiket2.setKorisnik(korisnikRepository.find(email));
			tiket2.setVozilo(null);
			model.addAttribute("rsAtribut", rRepository.findAllByJeAktivnoTrueOrderByRegDoDesc());
		}
		tiket2.setTiketDatum(new Date());
		model.addAttribute("tAtribut", tiket2);
		
		return "korisnik/tiket/novi";
	}
	
	@RequestMapping(value="/korisnik/tiketi/novi", method = RequestMethod.POST)
	public String postNoviTiket(@ModelAttribute("tAtribut") @Valid Tiket2 tiket2, Principal principal,  BindingResult rezultat, Model model){
		
		if(rezultat.hasErrors()){
			String email = principal.getName();
			KorisnikVozilo kvTemp = new KorisnikVozilo();
			Tiket2 newTiket2 = new Tiket2();
			try {
				kvTemp = kvRepository.findByKorisnik_EmailAndVracenoNull(email);
				newTiket2.setKorisnik(kvTemp.getKorisnik());
				newTiket2.setVozilo(vRepository.findOne(kvTemp.getVozilo().getVin()));
				model.addAttribute("rAtribut", rRepository.findByVozilo_VinAndJeAktivnoTrue(kvTemp.getVozilo().getVin()));
				System.out.println("dodao korisnika: "+kvTemp.getKorisnik().getEmail());
			} catch (Exception e) {
				tiket2.setKorisnik(korisnikRepository.find(email));
				tiket2.setVozilo(null);
				model.addAttribute("rsAtribut", rRepository.findAllByJeAktivnoTrueOrderByRegDoDesc());
			}
			
			model.addAttribute("tAtribut", tiket2);
			
			return "korisnik/tiket/novi";
		}
		tiket2Repository.saveAndFlush(tiket2);
		
			return "korisnik/tiketi/poruke/novi";
	}

}

package ba.fit.vms.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Poruka;
import ba.fit.vms.pojo.Tiket2;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.PorukaRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.Tiket2Repository;
import ba.fit.vms.repository.VoziloRepository;

@Controller
public class Tiket2Controller {
	
	@Autowired
	private Tiket2Repository tiket2Repository;
	
	@Autowired
	private PorukaRepository pRepository;
	
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
		tiket2.setTiketDatum(new DateTime().toDate());
		Poruka prva = new Poruka();
		prva.setDatum(new DateTime().toDate());
		List<Poruka> poruke = new ArrayList<Poruka>();
		poruke.add(prva);
		tiket2.setPoruke(poruke);
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
		System.out.println(new DateTime().toDate());
		System.out.println(new DateTime());
		model.addAttribute("tAtribut", tiket2);
		model.addAttribute("pAtribut", prva);
		
		return "korisnik/tiket/novi";
	}
	
	@RequestMapping(value="/korisnik/tiketi/novi", method = RequestMethod.POST)
	public String postNoviTiket(@ModelAttribute("pAtribut") @Valid Poruka poruka,  BindingResult porukaRezultat, @ModelAttribute("tAtribut") @Valid Tiket2 tiket2,  BindingResult tiketRezultat, Principal principal,  Model model){
		
		if(tiketRezultat.hasErrors() || porukaRezultat.hasErrors()){
			String email = principal.getName();
			KorisnikVozilo kvTemp = new KorisnikVozilo();
			System.out.println(tiketRezultat.toString());
			System.out.println(porukaRezultat.toString());
			try {
				kvTemp = kvRepository.findByKorisnik_EmailAndVracenoNull(email);
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
		pRepository.saveAndFlush(poruka);
		List<Poruka> poruke = new ArrayList<Poruka>();
		poruke.add(poruka);
		tiket2.setPoruke(poruke);
		tiket2Repository.saveAndFlush(tiket2);
		
			return "redirect:/korisnik/tiketi/";
	}
	
	@RequestMapping(value="/korisnik/tiketi/", method=RequestMethod.GET)
	public String getListaTiketa(Principal principal, HttpServletRequest request,  Model model){
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		
		Korisnik  k = korisnikRepository.find(principal.getName());
		Page<Tiket2> pages = tiket2Repository.findByRijesenDatumIsNullAndKorisnikOrderByTiketDatumDesc(k, pageable);
		for (Tiket2 tiket2 : pages) {
			System.out.println(tiket2.getPoruke().size());			
		}
		
		
		model.addAttribute("pager", pages);
		
		return "/korisnik/tiket/listaSvih";
	}

}

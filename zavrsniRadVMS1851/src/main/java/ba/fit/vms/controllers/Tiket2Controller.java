package ba.fit.vms.controllers;

import java.security.Principal;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

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
@SessionAttributes("userAtribut")
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
	
	@RequestMapping(value={"/korisnik/{kid}/tiketi/novi"}, method = RequestMethod.GET)
	public String getNoviTiket(@PathVariable("kid") Long kid, @RequestParam(value="vin", required=true) String vin, Model model){
		Korisnik k;
		System.out.println("kid = "+kid);
		System.out.println("vin = "+vin);
		if(kid!=null){
			k = korisnikRepository.findOne(kid);
		}else{
			k = kvRepository.findByVozilo_VinAndVracenoNull(vin).getKorisnik();
		}
		System.out.println(k.getEmail());
		KorisnikVozilo kvTemp = new KorisnikVozilo();
		Tiket2 tiket2 = new Tiket2();
		tiket2.setTiketDatum(new DateTime().toDate());
		Poruka prva = new Poruka();
		prva.setDatum(new DateTime().toDate());
		prva.setKorisnik(k);
		//List<Poruka> poruke = new ArrayList<Poruka>();
		//poruke.add(prva);
		tiket2.getPoruke().add(prva);
		//tiket2.setPoruke(poruke);
		try {
			kvTemp = kvRepository.findByKorisnik_EmailAndVracenoNull(k.getEmail());
			tiket2.setKorisnik(kvTemp.getKorisnik());
			tiket2.setVozilo(kvTemp.getVozilo());
			model.addAttribute("rAtribut", rRepository.findByVozilo_VinAndJeAktivnoTrue(kvTemp.getVozilo().getVin()));
			System.out.println("dodao korisnika: "+kvTemp.getKorisnik().getEmail());
		} catch (Exception e) {
			tiket2.setKorisnik(k);
			tiket2.setVozilo(null);
			model.addAttribute("rsAtribut", rRepository.findAllByJeAktivnoTrueOrderByRegDoDesc());
		}
		System.out.println("tiket2 id = "+tiket2.getId());
		model.addAttribute("tAtribut", tiket2);
		System.out.println("poruka id = "+prva.getId());
		model.addAttribute("pAtribut", prva);
		
		return "korisnik/tiket/novi";
	}
	
	@RequestMapping(value="/korisnik/{kid}/tiketi/novi", method = RequestMethod.POST)
	public String postNoviTiket(@PathVariable("kid") Long kid, @ModelAttribute("pAtribut") @Valid Poruka poruka,  BindingResult porukaRezultat, @ModelAttribute("tAtribut") @Valid Tiket2 tiket2,  BindingResult tiketRezultat, Model model){
		System.out.println("tiket2 id = "+tiket2.getId());
		System.out.println("poruka id = "+poruka.getId());
		System.out.println("kid = "+kid);
		String link;
		if(kid!=null){
			link = "korisnik/"+kid.toString()+"/tiketi/";
		}else{
			link = "korisnik/tiketi/";
		}
		if(tiketRezultat.hasErrors() || porukaRezultat.hasErrors()){
			String email = korisnikRepository.findOne(kid).getEmail();
			KorisnikVozilo kvTemp = new KorisnikVozilo();
			System.out.println("tiket greske: " + tiketRezultat.toString());
			System.out.println("poruka greske: " + porukaRezultat.toString());
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
			model.addAttribute("pAtribut", poruka);
			
			return "/"+link;
		}
		List<Poruka> poruke = new ArrayList<Poruka>();
		if(!poruka.getSadrzaj().isEmpty()){
			//pRepository.saveAndFlush(poruka);
			poruke.add(poruka);
			tiket2.setPoruke(poruke);
			try {
				tiket2Repository.saveAndFlush(tiket2);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		
		
			return "redirect:/"+link;
	}
	
	@RequestMapping(value="/korisnik/tiketi/", method=RequestMethod.GET)
	public String getListaTiketa(Principal principal, HttpServletRequest request,  Model model){
		System.out.println("/korisnik/tiketi/");
		String email = principal.getName();
		System.out.println(email);
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 10;

		Pageable pageable = new PageRequest(page, pageSize);
		
		try {
			Page<Tiket2> pages = tiket2Repository.findAll(pageable);
			model.addAttribute("pager", pages);
		} catch (Exception e) {
			System.out.println("Poruka o gresci: "+e.getMessage());
		}
		
		model.addAttribute("kvAtribut", kvRepository.findByKorisnik_EmailAndVracenoNull(email));
		
		
		return "/korisnik/tiket/listaSvih";
	}
	@RequestMapping(value="/korisnik/{kid}/tiketi/", method=RequestMethod.GET)
	public String getListaKorisnikTiketa(@PathVariable("kid") Long kid, Principal principal, HttpServletRequest request,  Model model){
		System.out.println("/korisnik/{id}/tiketi/");
		Korisnik k = korisnikRepository.findOne(kid);
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 10;

		Pageable pageable = new PageRequest(page, pageSize);
		try {
			KorisnikVozilo  kv = kvRepository.findByKorisnik_EmailAndVracenoNull(principal.getName());
			Page<Tiket2> pages = tiket2Repository.findByKorisnikOrderByTiketDatumDesc(k, pageable);
			
			model.addAttribute("pager", pages);
			model.addAttribute("kvAtribut", kv);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "redirect:/korisnik/tiketi/";
		}
		return "/korisnik/tiket/lista";
	}
	
	@RequestMapping(value="/korisnik/{kid}/tiketi/otvoreni", method=RequestMethod.GET)
	public String getListaKorisnikTiketaOtvoreni(@PathVariable("kid") Long kid, Principal principal, HttpServletRequest request,  Model model){
		System.out.println("/korisnik/{id}/tiketi/otvoreni");
		Korisnik k = korisnikRepository.findOne(kid);
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 10;

		Pageable pageable = new PageRequest(page, pageSize);
		try {
			KorisnikVozilo  kv = kvRepository.findByKorisnik_EmailAndVracenoNull(principal.getName());
			Page<Tiket2> pages = tiket2Repository.findByRijesenDatumIsNullAndKorisnikOrderByTiketDatumDesc(k, pageable);
			
			model.addAttribute("pager", pages);
			model.addAttribute("kvAtribut", kv);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "redirect:/korisnik/tiketi/";
		}
		return "/korisnik/tiket/lista";
	}
	
	@RequestMapping(value="/korisnik/{kid}/tiketi/rijeseni", method=RequestMethod.GET)
	public String getListaKorisnikTiketaRijeseni(@PathVariable("kid") Long kid, Principal principal, HttpServletRequest request,  Model model){
		System.out.println("/korisnik/{id}/tiketi/rijeseni");
		Korisnik k = korisnikRepository.findOne(kid);
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 10;

		Pageable pageable = new PageRequest(page, pageSize);
		try {
			KorisnikVozilo  kv = kvRepository.findByKorisnik_EmailAndVracenoNull(principal.getName());
			Page<Tiket2> pages = tiket2Repository.findByRijesenDatumIsNotNullAndKorisnikOrderByTiketDatumDesc(k, pageable);
			
			model.addAttribute("pager", pages);
			model.addAttribute("kvAtribut", kv);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "redirect:/korisnik/tiketi/";
		}
		return "/korisnik/tiket/lista";
	}
	
	@RequestMapping(value="/korisnik/tiketi/otvoreni", method=RequestMethod.GET)
	public String getListaTiketaOtvoreni(HttpServletRequest request,  Model model){
		System.out.println("/korisnik/tiketi/otvoreni");
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 10;

		Pageable pageable = new PageRequest(page, pageSize);
		try {
			Page<Tiket2> pages = tiket2Repository.findByRijesenDatumIsNullOrderByTiketDatumDesc(pageable);
			
			model.addAttribute("pager", pages);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "redirect:/korisnik/tiketi/";
		}
		return "/korisnik/tiket/lista";
	}
	
	@RequestMapping(value="/korisnik/tiketi/rijeseni", method=RequestMethod.GET)
	public String getListaKorisnikTiketaRijeseni(HttpServletRequest request,  Model model){
		System.out.println("/korisnik/tiketi/rijeseni");
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 10;

		Pageable pageable = new PageRequest(page, pageSize);
		try {
			Page<Tiket2> pages = tiket2Repository.findByRijesenDatumIsNotNullOrderByTiketDatumDesc(pageable);
			
			model.addAttribute("pager", pages);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return "redirect:/korisnik/tiketi/";
		}
		return "/korisnik/tiket/lista";
	}

}

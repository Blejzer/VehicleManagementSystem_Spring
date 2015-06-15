package ba.fit.vms.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Tiket;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.TiketRepository;
import ba.fit.vms.repository.VoziloRepository;

@Controller
public class TiketController {
	
	@Autowired
	private TiketRepository tiketRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	@Autowired
	private KorisnikVoziloRepository korisnikVoziloRepository;
	
	@RequestMapping(value="/korisnik/tiketi/novi", method = RequestMethod.GET)
	public String getNoviTiket(Principal principal, Model model){
		String email = principal.getName();
		KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(email);
		Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());
		Tiket tiket = new Tiket();
		tiket.setKorisnik(kv.getKorisnik());
		tiket.setTiketDatum(new Date());
		tiket.setVozilo(reg.getVozilo());
		model.addAttribute("tAtribut", tiket);
		model.addAttribute("rAtribut", reg);
		model.addAttribute("kvAtribut", kv);
		return "korisnik/tiket/novi";
	}
	
	@RequestMapping(value={"/korisnik/tiketi/novi", "/korisnik/tiketi/nastavi"}, method=RequestMethod.POST)
	public String postNoviTiket(@ModelAttribute("tAtribut") @Valid Tiket tiket, Principal principal, Model model, BindingResult rezultat){
		
		if(rezultat.hasErrors()){
			String email = principal.getName();
			KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(email);
			Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());
			model.addAttribute("rAtribut", reg);
			model.addAttribute("kvAtribut", kv);
			return "/korisnik/tiket/novi";
		}
		tiketRepository.save(tiket);
		return "redirect:/korisnik/tiketi/lista?vin="+tiket.getVozilo().getVin();
	}
	
	@RequestMapping(value="/korisnik/tiketi/odgovor", method = RequestMethod.GET)
	public String getOdgovorTiket(@RequestParam(value="id", required=true) Long id, Principal principal, Model model){
		
		Tiket tiket = tiketRepository.findOne(id);
		tiket.setRijesenDatum(new Date());
		model.addAttribute("tAtribut", tiket);
		model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(tiket.getVozilo().getVin()));
		model.addAttribute("kvAtribut", korisnikVoziloRepository.findByVozilo_VinAndVracenoNull(tiket.getVozilo().getVin()));
		return "korisnik/tiket/odgovor";
	}
	
	@RequestMapping(value="/korisnik/tiketi/odgovor", method=RequestMethod.POST)
	public String postOdgovorTiket(@ModelAttribute("tAtribut") @Valid Tiket tiket, Principal principal, Model model, BindingResult rezultat){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(!(auth.getAuthorities().toString().equalsIgnoreCase("[ROLE_ADMIN]"))){
			rezultat.rejectValue("korisnik", "korisnik.NijeAdmin",
                    "Da bi ste odgovorili na ovaj tiket morate biti administrator");
		}
		if(tiket.getOdgovor().isEmpty() || tiket.getOdgovor()==null){
			rezultat.rejectValue("odgovor", "odgovor.EmptyOrNull",
                    "Morate unijeti odgovor na ovaj tiket da bi ste ga zatvorili");
		}
		if(rezultat.hasErrors()){
			model.addAttribute("rAtribut", registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(tiket.getVozilo().getVin()));
			model.addAttribute("kvAtribut", korisnikVoziloRepository.findByVozilo_VinAndVracenoNull(tiket.getVozilo().getVin()));
			return "/korisnik/tiket/odgovor";
		}
		tiketRepository.save(tiket);
		return "redirect:/korisnik/tiketi/lista";
	}
	
	@RequestMapping(value="/korisnik/tiketi/nastavi", method=RequestMethod.GET)
	public String getNastaviTiket(@RequestParam(value="id", required=true) Long id, Principal principal, Model model){
		
		Tiket prethodni = tiketRepository.findOne(id);
		Tiket tiket = new Tiket();
		tiket.setPrethodni(prethodni);
		tiket.setNaslov(prethodni.getNaslov());
		String email = principal.getName();
		KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(email);
		Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());
		tiket.setKorisnik(kv.getKorisnik());
		tiket.setTiketDatum(new Date());
		tiket.setVozilo(reg.getVozilo());
		model.addAttribute("tAtribut", tiket);
		model.addAttribute("rAtribut", reg);
		model.addAttribute("kvAtribut", kv);		
		
		return "/korisnik/tiket/nastavi";
	}

	
	@RequestMapping(value="/korisnik/tiketi/lista", method=RequestMethod.GET)
	public String getTestTiketi(HttpServletRequest request, Principal principal, Model model){
		List<Tiket> vezani = new ArrayList<Tiket>();
		List<Tiket> nevezani = new ArrayList<Tiket>();
		LinkedHashMap<Tiket, List<Tiket>> report = new LinkedHashMap<Tiket, List<Tiket>>();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth.getAuthorities().toString().equalsIgnoreCase("[ROLE_USER]")){
			String email = principal.getName();
			KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(email);
			Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());
			
			vezani = tiketRepository.findAllByKorisnik_IdAndVozilo_vinAndPrethodniNotNullOrderByIdDesc(korisnikRepository.find(principal.getName()).getId(), kv.getVozilo().getVin());
			nevezani = tiketRepository.findAllByKorisnik_IdAndVozilo_vinAndPrethodniNullOrderByIdDesc(korisnikRepository.find(principal.getName()).getId(), kv.getVozilo().getVin());
			
			
			
			System.out.println("nevezani: "+ nevezani.size());
			System.out.println("vezani: "+ vezani.size());

			model.addAttribute("rAtribut", reg);
			model.addAttribute("kvAtribut", kv);
		
		
		}else{
			vezani = tiketRepository.findAllByPrethodniNotNullOrderByIdDesc();
			nevezani = tiketRepository.findAllByPrethodniNullOrderByIdDesc();
			System.out.println("nevezani: "+ nevezani.size());
			System.out.println("vezani: "+ vezani.size());
		}
		
		for (Tiket tiket : nevezani) {
			List<Tiket> konacni = new ArrayList<Tiket>();
			konacni.add(tiket);
			Tiket test = tiket;
			int brojac=0;
			do {
				if(test.getId()==vezani.get(brojac).getPrethodni().getId()){
					konacni.add(vezani.get(brojac));
					test = vezani.get(brojac);
					brojac = 0;
				}else{
					brojac++;
					System.out.println(brojac);
				}
			} while ((brojac)<vezani.size());
			System.out.println("konacni: "+ konacni.size());
			report.put(tiket, konacni);
			
		}
			
		System.out.println("report: "+ report.size());
		model.addAttribute("report", report);
		
		return "/korisnik/tiket/listaSvih";
	}
	
	
	@RequestMapping(value="/korisnik/tiketi/rijeseni", method=RequestMethod.GET)
	public String getListaOdgovorenihTiketa(HttpServletRequest request, Principal principal, Model model){
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}
		int pageSize = 4;

	    Pageable pageable = new PageRequest(page, pageSize);
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth.getAuthorities().toString().equalsIgnoreCase("[ROLE_USER]")){
			String email = principal.getName();
			KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(email);
			Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());
			model.addAttribute("rAtribut", reg);
			model.addAttribute("kvAtribut", kv);
			model.addAttribute("pager", tiketRepository.findAllByKorisnik_IdAndRijesenDatumNotNullOrderByTiketDatumDesc(korisnikRepository.find(principal.getName()).getId(), pageable));
			return "/korisnik/tiket/lista";
		}else{
			model.addAttribute("pager", tiketRepository.findAllByRijesenDatumNotNullOrderByTiketDatumDesc(pageable));
			return "/korisnik/tiket/lista";
		}
		
	}
	
	@RequestMapping(value="/korisnik/tiketi/nerijeseni", method=RequestMethod.GET)
	public String getListaNeodgovoenihTiketa(HttpServletRequest request, Principal principal, Model model){
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}
		int pageSize = 4;

	    Pageable pageable = new PageRequest(page, pageSize);
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    if(auth.getAuthorities().toString().equalsIgnoreCase("[ROLE_USER]")){
	    	String email = principal.getName();
			KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(email);
			Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());
			model.addAttribute("rAtribut", reg);
			model.addAttribute("kvAtribut", kv);
			model.addAttribute("pager", tiketRepository.findAllByKorisnik_IdAndRijesenDatumNullOrderByTiketDatumDesc(korisnikRepository.find(principal.getName()).getId(), pageable));
			return "/korisnik/tiket/lista";
		}else{
			model.addAttribute("pager", tiketRepository.findAllByRijesenDatumNullOrderByTiketDatumDesc(pageable));
			return "/korisnik/tiket/lista";
		}
		
	}
	
	@RequestMapping(value="/korisnik/tiketi/tiket", method = RequestMethod.GET)
	public String getTiketiDetaljno(HttpServletRequest request, Principal principal, Model model){
		int page;
		int brojac=0;
		System.out.println(Long.parseLong(request.getParameter("id")));
		List<Tiket> vezani = new ArrayList<Tiket>();
		List<Tiket> konacni = new ArrayList<Tiket>();
		Tiket test = tiketRepository.findOne(Long.parseLong(request.getParameter("id")));
		konacni.add(test);
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}
		int pageSize = 4;

	    Pageable pageable = new PageRequest(page, pageSize);
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth.getAuthorities().toString().equalsIgnoreCase("[ROLE_USER]")){
			String email = principal.getName();
			KorisnikVozilo kv = korisnikVoziloRepository.findByKorisnik_EmailAndVracenoNull(email);
			Registracija reg = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());
			model.addAttribute("rAtribut", reg);
			model.addAttribute("kvAtribut", kv);
			vezani = tiketRepository.findAllByKorisnik_IdAndVozilo_vinAndPrethodniNotNullOrderByIdDesc(korisnikRepository.find(principal.getName()).getId(), kv.getVozilo().getVin());
			
		}else{
			vezani = tiketRepository.findAllByPrethodniNotNullOrderByIdDesc();
		}
		do {
			if(test.getId()==vezani.get(brojac).getPrethodni().getId()){
				konacni.add(vezani.get(brojac));
				test = vezani.get(brojac);
				brojac = 0;
			}else{
				brojac++;
				System.out.println(brojac);
			}
		} while ((brojac)<vezani.size());
		
		List<Long> ids = new ArrayList<Long>();
		
		for (Tiket tiket : konacni) {
			ids.add(tiket.getId());
		}

		model.addAttribute("pager", tiketRepository.findAllByIdIn(ids, pageable));
		
		return "/korisnik/tiket/lista";
	}

}

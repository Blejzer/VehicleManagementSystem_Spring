package ba.fit.vms.controllers;

import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.util.KorisnikValidatorForme;

@Controller
@SessionAttributes("userAtribut")
public class KorisnikWebController {

	@Inject
	private PasswordEncoder passwordEncoder;
	
	protected static Logger logger = Logger.getLogger("controller");
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private KorisnikVoziloRepository kvRepository;
	
	@Autowired
	private RegistracijaRepository rRepository;

	@Autowired
	private KorisnikValidatorForme korisnikValidatorForme;
	

	/**
	 * Mapiramo view nakon uspjesnog logina koji nam proslijedjuje SigninController
	 * 
	 * @return
	 */
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String welcome() {
		return "admin/welcome";
	}


	/**
	 * Mapiramo otvaranje forme za dodavanje novog korisnika
	 * prije vracanja forme, provjeravamo da li korisnik ima permisije 'isUserInRole("ROLE_ADMIN")'
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/korisnici/novi", method = RequestMethod.GET)
	public String getNoviKorisnik(SecurityContextHolderAwareRequestWrapper request, Model model){

		Korisnik k = (Korisnik) request.getSession().getAttribute("userAtribut");
		if(k.getRola().equalsIgnoreCase("ROLE_ADMIN")){
			model.addAttribute("korisnikAtribut", new Korisnik());
			return "/admin/korisnik/novi";

		}
		return "/auth/zabranjenPristup";
	}


	/**
	 * Provjeravamo da li korisnik ima permisije
	 * Mapiramo snimanje podataka o novom korisniku dobivenih iz forme
	 * ukoliko Korisnik nije validan, vraca na formu i ukazuje na gresku
	 * snimamo novog korisnika i preusmjeravamo na listu korisnika
	 * @param korisnik
	 * @param rezultat
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/admin/korisnici/novi", method = RequestMethod.POST)
	public String postNoviKorisnik(@ModelAttribute("korisnikAtribut") @Valid Korisnik korisnik, BindingResult rezultat, SecurityContextHolderAwareRequestWrapper request){
		if(request.isUserInRole("ROLE_ADMIN")){

			System.out.println("usao u save");
			/*
			 * Override validacije koristeci validaciju koju smo napravili
			 * provjeravamo da li postoji korisnik sa istim email-om
			 * jer nam je email login podatak i mora biti jedinstven u bazi
			 * 
			 */
			korisnikValidatorForme.validate(korisnik, rezultat);
			if (rezultat.hasErrors()) 
			{
				return "/admin/korisnik/novi";
			}
			korisnik.setLozinka(passwordEncoder.encode(korisnik.getLozinka()));
			
			korisnikRepository.save(korisnik);
			return "redirect:/admin/korisnici/";

		}
		return "/auth/zabranjenPristup";
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/admin/korisnici/delete", method = RequestMethod.GET)
	public String getIzbrisiKorisnika(@RequestParam(value="id", required=true) Long id, HttpServletRequest request, HttpServletResponse response, Model model){
		try {
			korisnikRepository.delete(id);
			return "redirect:/admin/korisnici/";
		} catch (DataIntegrityViolationException ex) {
			String page = request.getParameter("page");
			PagedListHolder korisnici = (PagedListHolder) request.getSession().getAttribute("KorisnikWebController_korisnici");
			if (korisnici == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo pokusajte ponovo");
			}
			else
			{
				korisnici.setPage(Integer.parseInt(page));
				model.addAttribute("pager", korisnici);
				model.addAttribute("error", ex.getLocalizedMessage());
			}
			return "/admin/korisnici/";
		}
	}


	@RequestMapping(value="/admin/korisnici/izmjeni", method = RequestMethod.GET)
	public String getIzmjeniKorisnika(@RequestParam(value="id", required=true) Long id, HttpServletRequest request, HttpServletResponse response, Model model){

		Korisnik korisnik = korisnikRepository.findOne(id);
		korisnik.setId(id);
		korisnik.setLozinka(null);
		model.addAttribute("korisnikAtribut", korisnik);

		return "/admin/korisnik/izmjena";
	}

	@RequestMapping(value="/admin/korisnici/izmjeni", method = RequestMethod.POST)
	public String postIzmjeniKorisnika(@ModelAttribute("korisnikAtribut") @Valid Korisnik korisnik, BindingResult rezultat, SecurityContextHolderAwareRequestWrapper request){
		
		if(request.isUserInRole("ROLE_ADMIN")){
			
			Korisnik stari = korisnikRepository.findOne(korisnik.getId());
			if(!(stari.getEmail().equals(korisnik.getEmail()))){
				korisnikValidatorForme.validate(korisnik, rezultat);
			}
			

			if (rezultat.hasErrors()) 
			{
				return "/admin/korisnik/izmjena";
			}
			if(korisnik.getLozinka()==null || korisnik.getLozinka().isEmpty()){
				korisnik.setLozinka(stari.getLozinka());
			}else{
				korisnik.setLozinka(passwordEncoder.encode(korisnik.getLozinka()));
				
			}
			korisnikRepository.save(korisnik);

			return "redirect:/admin/korisnici/";

		}
		return "/auth/zabranjenPristup";
	}

	/**
	 * Mapiramo listu svih korisnika na dvije adrese '/admin/korisnici/ i /admin/korisnici/lista'
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"/admin/korisnici/", "/admin/korisnici/lista"}, method = RequestMethod.GET)
	public String getSviKorisnici(HttpServletRequest request, Model model){
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		//model.addAttribute("pager", korisnikRepository.findAll(pageable));
		Page<Korisnik> korisnici = korisnikRepository.findAll(pageable);
		TreeMap<Korisnik, Registracija> pregled2 = new TreeMap<Korisnik, Registracija>();
		for (Korisnik korisnik : korisnici) {
			try {
				KorisnikVozilo kv = kvRepository.findByKorisnik_IdAndVracenoNull(korisnik.getId());
				Registracija r = rRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());

				pregled2.put(korisnik, r);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				KorisnikVozilo kv = new KorisnikVozilo();
				kv.setKorisnik(korisnik);
				pregled2.put(korisnik, null);
			}
		}
		model.addAttribute("pager", korisnici);
		model.addAttribute("korAtribut", pregled2);
		return "/admin/korisnik/lista";
	}
	
	/**
	 * Get za pretrazivanje korisnika!
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"/admin/korisnici/pretraga1"}, method = RequestMethod.GET)
	public String getPretraga1Korisnici(HttpServletRequest request, HttpServletResponse response, Model model){

		System.out.println("Get pretraga1");
		System.out.println("letter: "+request.getParameter("letter"));
		System.out.println("page: "+request.getParameter("page"));
		
		String letter = "";
		
		if(request.getParameter("letter")!=null){
			letter = request.getParameter("letter");
		}
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}
		int pageSize = 4;
		Pageable pageable = new PageRequest(page, pageSize);
		
		if(letter!=""){
			String l = letter+"%";
			try {
				Page<Korisnik> korisnici = korisnikRepository.findByImeLikeOrPrezimeLike(l, l, pageable);
				TreeMap<Korisnik, Registracija> pregled2 = new TreeMap<Korisnik, Registracija>();
				if(korisnici.getTotalElements()>0){
					for (Korisnik korisnik : korisnici) {
						try {
							KorisnikVozilo kv = kvRepository.findByKorisnik_IdAndVracenoNull(korisnik.getId());
							Registracija r = rRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());

							pregled2.put(korisnik, r);
						} catch (Exception e) {
							System.out.println(e.getMessage());
							KorisnikVozilo kv = new KorisnikVozilo();
							kv.setKorisnik(korisnik);
							pregled2.put(korisnik, null);
						}
					}
					model.addAttribute("korAtribut", pregled2);
					System.out.println("uradio slovo: "+pregled2.size());
				}else{
					model.addAttribute("korAtribut", null);					
				}
				model.addAttribute("pager", korisnici);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		}else{
			return"redirect:/admin/korisnici/";
		}
		model.addAttribute("letter", letter);
		return "/admin/korisnik/lista";
	}
	
	/**
	 * Get za pretrazivanje korisnika!
	 * @param model
	 * @return
	 */
	@RequestMapping(value = {"/admin/korisnici/pretraga2"}, method = RequestMethod.GET)
	public String getPretraga2Korisnici(HttpServletRequest request, Model model){

		System.out.println("Get pretraga2");
		System.out.println("name: "+request.getParameter("name"));
		System.out.println("isActive: "+request.getParameter("isActive"));
		System.out.println("page: "+request.getParameter("page"));
		
		String name = "";
		String isActiveString = "sve";
		
		if(request.getParameter("isActive").compareTo(isActiveString)!=0){
			isActiveString = request.getParameter("isActive");
		}
		
		if(request.getParameter("name")!=null){
			name = request.getParameter("name");
		}
		
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}
		int pageSize = 4;
		Pageable pageable = new PageRequest(page, pageSize);
		System.out.println("page: "+pageable.getPageNumber());
		
		if(name!=""){
			String imePrezime = "%"+name+"%";
			if(isActiveString!="sve"){
				Boolean isActive = Boolean.valueOf(isActiveString);
				
				try {
					Page<Korisnik> korisnici = korisnikRepository.findByImeLikeOrPrezimeLikeAndJeAktivan(imePrezime, imePrezime, isActive, pageable);
					TreeMap<Korisnik, Registracija> pregled2 = new TreeMap<Korisnik, Registracija>();
					if(korisnici.getTotalElements()>0){
						for (Korisnik korisnik : korisnici) {
							try {
								KorisnikVozilo kv = kvRepository.findByKorisnik_IdAndVracenoNull(korisnik.getId());
								Registracija r = rRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());

								pregled2.put(korisnik, r);
							} catch (Exception e) {
								System.out.println(e.getMessage());
								KorisnikVozilo kv = new KorisnikVozilo();
								kv.setKorisnik(korisnik);
								pregled2.put(korisnik, null);
							}
						}
						model.addAttribute("korAtribut", pregled2);
						System.out.println("uradio ime/prezime: "+pregled2.size());
					}else{
						model.addAttribute("korAtribut", null);					
					}
					model.addAttribute("pager", korisnici);
					System.out.println("uradio imePrezime i jeAktivno: "+imePrezime+" "+request.getParameter("isActive"));

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			} else{
				try {
					Page<Korisnik> korisnici = korisnikRepository.findByImeLikeOrPrezimeLike(imePrezime, imePrezime, pageable);
					TreeMap<Korisnik, Registracija> pregled2 = new TreeMap<Korisnik, Registracija>();
					if(korisnici.getTotalElements()>0){
						for (Korisnik korisnik : korisnici) {
							try {
								KorisnikVozilo kv = kvRepository.findByKorisnik_IdAndVracenoNull(korisnik.getId());
								Registracija r = rRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());

								pregled2.put(korisnik, r);
							} catch (Exception e) {
								System.out.println(e.getMessage());
								KorisnikVozilo kv = new KorisnikVozilo();
								kv.setKorisnik(korisnik);
								pregled2.put(korisnik, null);
							}
						}
						model.addAttribute("korAtribut", pregled2);
						System.out.println("uradio ime/prezime: "+pregled2.size());
					}else{
						model.addAttribute("korAtribut", null);					
					}
					model.addAttribute("pager", korisnici);
					System.out.println("uradio imePrezime i jeAktivno: "+imePrezime+" "+request.getParameter("isActive"));

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}else{
			if(isActiveString!="sve"){
				try{
				System.out.println("isActiveString: "+isActiveString);
				Boolean isActive = Boolean.valueOf(request.getParameter("isActive"));
				Page<Korisnik> korisnici = korisnikRepository.findByJeAktivan(isActive, pageable);
				TreeMap<Korisnik, Registracija> pregled2 = new TreeMap<Korisnik, Registracija>();
				if(korisnici.getTotalElements()>0){
					for (Korisnik korisnik : korisnici) {
						try {
							KorisnikVozilo kv = kvRepository.findByKorisnik_IdAndVracenoNull(korisnik.getId());
							Registracija r = rRepository.findByVozilo_VinAndJeAktivnoTrue(kv.getVozilo().getVin());

							pregled2.put(korisnik, r);
						} catch (Exception e) {
							System.out.println(e.getMessage());
							KorisnikVozilo kv = new KorisnikVozilo();
							kv.setKorisnik(korisnik);
							pregled2.put(korisnik, null);
						}
					}
					model.addAttribute("korAtribut", pregled2);
					System.out.println("uradio jeAktivno: "+pregled2.size());
				}else{
					model.addAttribute("korAtribut", null);					
				}
				model.addAttribute("pager", korisnici);
				System.out.println("uradio jeAktivno: "+request.getParameter("isActive"));

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
				
			} else{
				return "redirect:/admin/korisnici/";
			}
		}
		model.addAttribute("name", name);
		model.addAttribute("isActive", isActiveString);
		return "/admin/korisnik/lista";
	}
}

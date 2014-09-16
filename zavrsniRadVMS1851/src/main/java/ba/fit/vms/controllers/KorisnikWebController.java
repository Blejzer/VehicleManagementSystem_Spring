package ba.fit.vms.controllers;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.util.KorisnikValidatorForme;

@Controller
public class KorisnikWebController {

	@Inject
	private PasswordEncoder passwordEncoder;
	
	protected static Logger logger = Logger.getLogger("controller");
	@Autowired
	private KorisnikRepository korisnikRepository;

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

		if(request.isUserInRole("ROLE_ADMIN")){
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
			
			System.out.println("lozinka u orginalu: " + korisnik.getLozinka());
			System.out.println("lozinka u orginalu: " + korisnik.getLozinka().isEmpty());
			
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
				System.out.println("lozinka nije enkodirana. stara lozinka je: " + stari.getLozinka());
			}else{
				korisnik.setLozinka(passwordEncoder.encode(korisnik.getLozinka()));
				System.out.println("lozinka enkodirana u: " + korisnik.getLozinka());
				
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = {"/admin/korisnici/", "/admin/korisnici/lista"}, method = RequestMethod.GET)
	public String getSviKorisnici(HttpServletRequest request, HttpServletResponse response, Model model){

		if(request.getParameter("page")==null)
		{
			PagedListHolder korisnici = new PagedListHolder((List) korisnikRepository.findAll());
			korisnici.setPageSize(10);
			request.getSession().setAttribute("KorisnikWebController_korisnici", korisnici);
			model.addAttribute("pager", korisnici);

			return "/admin/korisnik/lista";
		}	
		else 
		{
			String page = request.getParameter("page");
			PagedListHolder consultants = (PagedListHolder) request.getSession().getAttribute("KorisnikWebController_korisnici");
			if (consultants == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo ponovite Vasu pretragu");
			}
			else
			{
				consultants.setPage(Integer.parseInt(page));
				model.addAttribute("pager", consultants);
			}
			return "/admin/korisnik/lista";
		}
	}

}

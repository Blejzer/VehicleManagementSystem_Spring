package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.util.KorisnikValidatorForme;

@Controller
public class KorisnikWebController {
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	@Autowired
	private KorisnikValidatorForme korisnikValidatorForme;
	
	
	@RequestMapping(value = "/admin", method = RequestMethod.GET)
	public String welcome() {
        return "admin/welcome";
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
			PagedListHolder korisnici = new PagedListHolder(korisnikRepository.getSviKorisnici());
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
			if (rezultat.hasErrors()) 
			{		
				return "/admin/korisnik/dio/novi";
			}
			
			korisnikRepository.save(korisnik);
			return "redirect:/admin/korisnici/";
			
		}
		return "/auth/zabranjenPristup";
	}

}

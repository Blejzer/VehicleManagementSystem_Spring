package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/admin/korisnici/", method = RequestMethod.GET)
	public String getSviKorisnici(HttpServletRequest request, HttpServletResponse response, Model model){
		
		if(request.getParameter("page")==null)
		{
			PagedListHolder korisnici = new PagedListHolder(korisnikRepository.getSviKorisnici());
			korisnici.setPageSize(10);
			request.getSession().setAttribute("KorisnikWebController_korisnici", korisnici);
			model.addAttribute("pager", korisnici);

			return "/admin/korisnik/list";
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
	
	
	@RequestMapping(value="/admin/korisnici/novi", method = RequestMethod.GET)
	public String getNoviKorisnik(SecurityContextHolderAwareRequestWrapper request, Model model){
		
		if(request.isUserInRole("ROLE_ADMIN")){
			model.addAttribute("korisnikAtribut", new Korisnik());
			return "/admin/korisnik/novi";
			
		}
		return "/auth/zabranjenPristup";
	}

}

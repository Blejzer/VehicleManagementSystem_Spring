package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.VoziloRepository;
import ba.fit.vms.util.VoziloValidatorForme;

@Controller
public class VoziloController {

	protected static Logger logger = Logger.getLogger("controller");

	@Autowired
	private VoziloRepository voziloRepository;

	@Autowired
	private VoziloValidatorForme voziloValidatorForme;


	/**
	 * Mapiramo listu svih vozila na adresi /admin/vozila/
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/admin/vozila/", method = RequestMethod.GET)
	public String getVozila(HttpServletRequest request, HttpServletResponse response, Model model){

		if(request.getParameter("page")==null)
		{
			PagedListHolder vozila = new PagedListHolder(voziloRepository.getSvaVozila());
			vozila.setPageSize(5);
			request.getSession().setAttribute("VoziloController_vozila", vozila);
			model.addAttribute("pager", vozila);
			//model.addAttribute("search", new VehicleSearch());

			return "/admin/vozila/lista";
		}
		else 
		{
			String page = request.getParameter("page");
			PagedListHolder vozila = (PagedListHolder) request.getSession().getAttribute("VoziloController_vozila");
			if (vozila == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo ponovite Vasu pretragu");
			}
			else
			{
				vozila.setPage(Integer.parseInt(page));
				model.addAttribute("pager", vozila);
				//model.addAttribute("search", new VehicleSearch());
			}
			return "/admin/vozila/lista";
		}
	}


	/**
	 * Mapiramo otvaranje forme za dodavanje novog vozila
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/vozila/novi", method = RequestMethod.GET)
	public String getDodajVozilo(Model model){

		model.addAttribute("voziloAtribut", new Vozilo());

		return "/admin/vozila/novi";
	}
	
	
	/**
	 * Mapiramo snimanje podataka o novom vozilu dobivenih iz forme
	 * ukoliko Vozilo nije validan, vraca na formu i ukazuje na gresku
	 * Ukoliko su podaci u redu, proslijedjujemo VIN vozila i preusmjeravamo 
	 * na stranicu koja ce provjeriti da li je vozilo registrovano
	 * @param vozilo
	 * @param rezultat
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/vozila/novi", method = RequestMethod.POST)
	public String postRegTest(@ModelAttribute("voziloAtribut") @Valid Vozilo vozilo, BindingResult rezultat, Model model){
		voziloValidatorForme.validate(vozilo, rezultat);
		if(rezultat.hasErrors()){
			return "/admin/vozila/novi";
		}

		voziloRepository.save(vozilo);
		model.addAttribute("voziloAtribut", vozilo);

		return "/admin/vozila/regtest";

	}
	
	/**
	 * Mapiramo provjeru da li je vozilo registrovano ili nije 
	 * @param vozilo
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/vozila/regtest", method = RequestMethod.GET)
	public String getRegTest(@ModelAttribute("voziloAtribut") Vozilo vozilo, Model model){
		
		model.addAttribute("voziloAtribut", vozilo);

		return "/admin/vozila/regtest";

	}
	
	
	/**
	 * Mapiramo otvaranje forme za izmjenu podataka postojeceg vozila
	 * podatak koji se ne moze mijenjati je VIN broj vozila
	 * @param vin
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/vozila/izmjena", method = RequestMethod.GET)
	public String getIzmjenaVozila(@RequestParam(value="vin", required=true) String vin, Model model){

		model.addAttribute("voziloAtribut", voziloRepository.read(vin));
		
		return "/admin/vozila/izmjena";
	}
	
	
	/**
	 * Mapiramo snimanje podataka o izmjenama dobivenih iz forme
	 * ukoliko podaci nisu validni, vracamo na formu i ukazujemo na greske
	 * snimamo promjene i preusmjeravamo na listu vozila
	 * @param vozilo
	 * @param rezultat
	 * @param status
	 * @return
	 */
	@RequestMapping(value="/admin/vozila/izmjena", method = RequestMethod.POST)
	public String postIzmjenaVozila(@ModelAttribute("voziloAtribut") @Valid Vozilo vozilo, BindingResult rezultat, SessionStatus status){
		
		if(rezultat.hasErrors()){
			return "/admin/vozila/izmjena";
		}
		
		voziloRepository.edit(vozilo);
		
		return "redirect:/admin/vozila/";
	}
	
	
	/**
	 * Mapiramo zahtjev za brisanjem odabranog vozila
	 * @param vin
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/admin/vozila/izbrisi", method=RequestMethod.GET)
	public String getIzbrisiVozilo(@RequestParam(value="vin", required=true) String vin, HttpServletRequest request, HttpServletResponse response, Model model){

		try {
			voziloRepository.delete(vin);
			return "redirect:/admin/vozila/";
		} catch (DataIntegrityViolationException e) {
			String page = request.getParameter("page");
			PagedListHolder vozlia = (PagedListHolder) request.getSession().getAttribute("VoziloController_vozila");
			if (vozlia == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo pokusajte ponovo");
			}
			else
			{
				vozlia.setPage(Integer.parseInt(page));
				model.addAttribute("pager", vozlia);
				model.addAttribute("error", e.getLocalizedMessage());
			}
			return "/admin/vozila/";
		}
	}

}

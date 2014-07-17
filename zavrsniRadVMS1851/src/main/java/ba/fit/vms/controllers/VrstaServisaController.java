package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import ba.fit.vms.pojo.VrstaServisa;
import ba.fit.vms.repository.VrstaServisaRepository;

@Controller
public class VrstaServisaController {
	
	@Autowired
	private VrstaServisaRepository vrstaServisaRepository;
	
	
	/**
	 * Mapiramo listu svih vrsta servisa na adresi /admin/vrstaServisa/ i /admin/vrstaServisa/lista
	 * stvarna lokacija html fajla nije vidljiva u address baru, a nalazi se na /admin/servis/vrstaServisa/lista.html
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value={"/admin/vrstaServisa/", "/admin/vrstaServisa/lista"}, method = RequestMethod.GET)
	public String getSveVrsteServisa(HttpServletRequest request, HttpServletResponse response, Model model){
		
		if(request.getParameter("page")==null)
		{
			PagedListHolder vrsteServisa = new PagedListHolder(vrstaServisaRepository.getSveVrsteServisa());
			vrsteServisa.setPageSize(5); // Podesavamo koliko dijelova zelimo po stranici
			request.getSession().setAttribute("VSController_vrsteServisa", vrsteServisa);
			model.addAttribute("pager", vrsteServisa);
			
			return "/admin/servis/vrstaServisa/lista";
		}
		else 
		{
			String page = request.getParameter("page");
			PagedListHolder dijelovi = (PagedListHolder) request.getSession().getAttribute("VSController_vrsteServisa");
			if (dijelovi == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo ponovite Vasu pretragu");
			}
			else
			{
				dijelovi.setPage(Integer.parseInt(page));
				model.addAttribute("pager", dijelovi);
			}
			return "/admin/servis/vrstaServisa/lista";
		}
	}
	
	/**
	 * Mapiramo otvaranje forme za dodavanje nove vrste servisa na adresi /admin/vrstaServisa/novi
	 * stvarna adresa je na adresi /admin/servis/vrstaServisa/novi
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/vrstaServisa/novi", method = RequestMethod.GET)
	public String getNovaVrstaServisa(Model model){
		
		model.addAttribute("vsAtribut", new VrstaServisa());
		
		return "/admin/servis/vrstaServisa/novi";
	}
	
	
	/**
	 * Mapiramo snimanje podataka o novoj vrsti servisa dobivenih iz forme na adresi /admin/vrstaServisa/novi
	 * ukoliko VrstaServisa nije validan, vraca na formu i ukazuje na gresku
	 * snimamo vrstu servisa i preusmjeravamo na listu vrsti servisa
	 * @param vrstaServisa
	 * @param rezultat
	 * @return
	 */
	@RequestMapping(value="/admin/vrstaServisa/novi", method = RequestMethod.POST)
	public String postDodajVrstuServisa(@ModelAttribute("vsAtribut") @Valid VrstaServisa vrstaServisa, BindingResult rezultat){
		
		if (rezultat.hasErrors()) 
		{		
			return "/admin/servis/vrstaServisa/novi";
		}
		
		vrstaServisaRepository.save(vrstaServisa);
		return "redirect:/admin/vrstaServisa/";
	}
	
	/**
	 * Mapiramo otvaranje forme za izmjenu podataka postojece vrste servisa na adresi admin/vrstaServisa/izmjena
	 * stvarna adresa je admin/servis/vrstaServisa/izmjena
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/vrstaServisa/izmjena", method = RequestMethod.GET)
	public String getIzmjenaVrsteServisa(@RequestParam(value="id", required=true) Long id, Model model){
	
		model.addAttribute("vsAtribut", vrstaServisaRepository.read(id));
		
		return "/admin/servis/vrstaServisa/izmjena";
	}
	
	/**
	 * Mapiramo snimanje podataka o izmjenama dobivenih iz forme na adresi /admin/vrstaServisa/izmjena
	 * ukoliko podaci nisu validni, vracamo na formu i ukazujemo na greske
	 * snimamo promjene i preusmjeravamo na listu vrsti servisa
	 * @param vrstaServisa
	 * @param rezultat
	 * @return
	 */
	@RequestMapping(value="/admin/vrstaServisa/izmjena", method = RequestMethod.POST)
	public String postIzmjenaVrsteServisa(@ModelAttribute("dioAtribut") @Valid VrstaServisa vrstaServisa, BindingResult rezultat){
		if(rezultat.hasErrors())
		{
			return "/admin/servis/vrstaServisa/izmjena";
		}
	
		vrstaServisaRepository.update(vrstaServisa);
		return "redirect:/admin/vrstaServisa/";
	}
	
	/**
	 * Mapiramo zahtjev za brisanjem vrste servisa na adresi /admin/vrstaServisa/izbrisi
	 * @param id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/admin/vrstaServisa/izbrisi", method = RequestMethod.GET)
	public String izbrisiVrstuServisa(@RequestParam(value="id", required=true) Long id, HttpServletRequest request, HttpServletResponse response, Model model){
		try {
			vrstaServisaRepository.delete(id);
			return "redirect:/admin/vrstaServisa/";
		} catch (DataIntegrityViolationException ex) {

			String page = request.getParameter("page");
			PagedListHolder dijelovi = (PagedListHolder) request.getSession().getAttribute("VSController_vrsteServisa");
			if (dijelovi == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo pokusajte ponovo");
			}
			else
			{
				dijelovi.setPage(Integer.parseInt(page));
				model.addAttribute("pager", dijelovi);
				model.addAttribute("error", ex.getLocalizedMessage());
			}
			return "/admin/servis/vrstaServisa/lista";
		}
	}
}

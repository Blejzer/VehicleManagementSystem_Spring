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

import ba.fit.vms.pojo.Dio;
import ba.fit.vms.repository.DioRepository;

@Controller
public class DioController {
	
	@Autowired
	DioRepository dioRepository;
	
	
	/**
	 * Mapiramo listu svih dijelova na adresi /admin/dio/ i /admin/dio/lista
	 * stvarna lokacija html fajla nije vidljiva u address baru, a nalazi se na /admin/servis/dio/lista.html
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value={"/admin/dio/", "/admin/dio/lista"}, method = RequestMethod.GET)
	public String getSviDijelovi(HttpServletRequest request, HttpServletResponse response, Model model){
		
		if(request.getParameter("page")==null)
		{
			PagedListHolder dijelovi = new PagedListHolder(dioRepository.getSviDijelovi());
			dijelovi.setPageSize(5); // Podesavamo koliko dijelova zelimo po stranici
			request.getSession().setAttribute("DioController_dijelovi", dijelovi);
			model.addAttribute("pager", dijelovi);
			
			return "/admin/servis/dio/lista";
		}
		else 
		{
			String page = request.getParameter("page");
			PagedListHolder dijelovi = (PagedListHolder) request.getSession().getAttribute("DioController_dijelovi");
			if (dijelovi == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo ponovite Vasu pretragu");
			}
			else
			{
				dijelovi.setPage(Integer.parseInt(page));
				model.addAttribute("pager", dijelovi);
			}
			return "/admin/servis/dio/lista";
		}
	}
	
	/**
	 * Mapiramo otvaranje forme za dodavanje novog dijela na adresi /admin/dio/noviDio
	 * stvarna adresa je na adresi /admin/servis/dio/noviDio
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/admin/dio/noviDio"}, method = RequestMethod.GET)
	public String getDodajDio(Model model){
		
		model.addAttribute("dioAtribut", new Dio());
		return "/admin/servis/dio/noviDio";
	}
	
	
	/**
	 * Mapiramo snimanje podataka o novom dijelu dobivenih iz forme na adresi /admin/dio/noviDio
	 * ukoliko Dio nije validan, vraca na formu i ukazuje na gresku
	 * snimamo dio i preusmjeravamo na listu dijelova
	 * @param dio
	 * @param rezultat
	 * @return
	 */
	@RequestMapping(value="/admin/dio/noviDio", method = RequestMethod.POST)
	public String postDodajDio(@ModelAttribute("dioAtribut") @Valid Dio dio, BindingResult rezultat){
		
		if (rezultat.hasErrors()) 
		{		
			return "/admin/servis/dio/noviDio";
		}
		
		dioRepository.save(dio);
		return "redirect:/admin/dio/";
	}
	
	
	/**
	 * Mapiramo otvaranje forme za izmjenu podataka postojeceg dijela na adresi admin/dio/izmjena
	 * stvarna adresa je admin/servis/dio/izmjena
	 * kupimo id dijela i dodajemo dio u model kao dioAtribut
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/dio/izmjena", method = RequestMethod.GET)
	public String getIzmjenaDijela(@RequestParam(value="id", required=true) Long id, Model model){
	
		model.addAttribute("dioAtribut", dioRepository.read(id));
		
		return "/admin/servis/dio/izmjena";
	}
	
	
	/**
	 * Mapiramo snimanje podataka o izmjenama dobivenih iz forme na adresi /admin/dio/izmjena
	 * ukoliko podaci nisu validni, vracamo na formu i ukazujemo na greske
	 * snimamo promjene i preusmjeravamo na listu dijelova
	 * @param dio
	 * @param rezultat
	 * @return
	 */
	@RequestMapping(value="/admin/dio/izmjena", method = RequestMethod.POST)
	public String postIzmjenaDijela(@ModelAttribute("dioAtribut") @Valid Dio dio, BindingResult rezultat){
		
		if(rezultat.hasErrors())
		{
			return "/admin/servis/dio/izmjena";
		}
	
		dioRepository.update(dio);
		return "redirect:/admin/dio/";
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/admin/dio/izbrisi", method = RequestMethod.GET)
	public String getIzbrisiDio(@RequestParam(value="id", required=true) Long id, HttpServletRequest request, HttpServletResponse response, Model model){
		try {
			dioRepository.delete(id);
			return "redirect:/admin/dio/";
		} catch (DataIntegrityViolationException ex) {

			String page = request.getParameter("page");
			PagedListHolder dijelovi = (PagedListHolder) request.getSession().getAttribute("DioController_dijelovi");
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
			return "/admin/servis/dio/lista";
		}
	}
	

}

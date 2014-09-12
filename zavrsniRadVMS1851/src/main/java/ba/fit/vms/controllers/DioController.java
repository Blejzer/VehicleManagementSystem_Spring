package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	@RequestMapping(value={"/admin/dio/", "/admin/dio/lista"}, method = RequestMethod.GET)
	public String getSviDijelovi(Model model, HttpServletRequest request){

		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", dioRepository.findAll(pageable));
		return "/admin/servis/dio/lista";
	}

	/**
	 * Mapiramo otvaranje forme za dodavanje novog dijela na adresi /admin/dio/novi
	 * stvarna adresa je na adresi /admin/servis/dio/novi
	 * @param model
	 * @return
	 */
	@RequestMapping(value={"/admin/dio/novi"}, method = RequestMethod.GET)
	public String getDodajDio(Model model){

		model.addAttribute("dioAtribut", new Dio());
		return "/admin/servis/dio/novi";
	}


	/**
	 * Mapiramo snimanje podataka o novom dijelu dobivenih iz forme na adresi /admin/dio/novi
	 * ukoliko Dio nije validan, vraca na formu i ukazuje na gresku
	 * snimamo dio i preusmjeravamo na listu dijelova
	 * @param dio
	 * @param rezultat
	 * @return
	 */
	@RequestMapping(value="/admin/dio/novi", method = RequestMethod.POST)
	public String postDodajDio(@ModelAttribute("dioAtribut") @Valid Dio dio, BindingResult rezultat){

		if (rezultat.hasErrors()) 
		{		
			return "/admin/servis/dio/novi";
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

		model.addAttribute("dioAtribut", dioRepository.findOne(id));

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
		dioRepository.save(dio);
		return "redirect:/admin/dio/";
	}

	/**
	 * Mapiramo zahtjev za brisanjem odabranog dijela na adresi /admin/dio/izbrisi
	 * @param id
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/admin/dio/izbrisi", method = RequestMethod.GET)
	public String getIzbrisiDio(@RequestParam(value="id", required=true) Long id, HttpServletRequest request, Model model){
		try {
			dioRepository.delete(id);
			return "redirect:/admin/dio/";
		} catch (DataIntegrityViolationException ex) {

			int page;
			
			if(request.getParameter("page")==null){
				page=0;
			} else{
				page = Integer.parseInt(request.getParameter("page"));
			}

			int pageSize = 4;

			Pageable pageable = new PageRequest(page, pageSize);
			model.addAttribute("pager", dioRepository.findAll(pageable));
			return "/admin/servis/dio/lista";
		}
	}


}

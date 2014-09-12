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

import ba.fit.vms.pojo.Lokacija;
import ba.fit.vms.repository.LokacijaRepository;

@Controller
public class LokacijaController {
	
	@Autowired
	private LokacijaRepository lokacijaRepository;
	
	
	@RequestMapping(value={"/admin/lokacije/", "/admin/lokacije/lista"}, method = RequestMethod.GET)
	public String getSveVrsteServisa(Model model, HttpServletRequest request){
		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}
		
	    int pageSize = 4;

	    Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", lokacijaRepository.findAll(pageable));
		
		return "/admin/lokacija/lista";
	
	}
	
	
	@RequestMapping(value="/admin/lokacije/novi", method = RequestMethod.GET)
	public String getNovaLokacija(Model model){
		
		model.addAttribute("lokacijaAtribut", new Lokacija());
		
		return "/admin/lokacija/novi";
	}
	
	@RequestMapping(value="/admin/lokacije/novi", method = RequestMethod.POST)
	public String postNovaLokacija(@ModelAttribute("lokacijaAtribut") @Valid Lokacija lokacija, BindingResult rezultat){
		if (rezultat.hasErrors()) 
		{		
			return "/admin/lokacija/novi";
		}
		lokacijaRepository.save(lokacija);
		return "redirect:/admin/lokacije/";
	}
	
	@RequestMapping(value="/admin/lokacije/izbrisi", method = RequestMethod.GET)
	public String getIzbrisiDio(@RequestParam(value="id", required=true) Long id, HttpServletRequest request, Model model){
		try {
			lokacijaRepository.delete(id);
			return "redirect:/admin/lokacije/";
		} catch (DataIntegrityViolationException ex) {

			int page;
			
			if(request.getParameter("page")==null){
				page=0;
			} else{
				page = Integer.parseInt(request.getParameter("page"));
			}

			int pageSize = 4;

			Pageable pageable = new PageRequest(page, pageSize);
			model.addAttribute("pager", lokacijaRepository.findAll(pageable));
			return "/admin/lokacija/lista";
		}
	}

}

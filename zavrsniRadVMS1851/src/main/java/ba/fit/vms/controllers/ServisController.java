package ba.fit.vms.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.repository.ServisRepository;

@Controller
public class ServisController {
	
	@Autowired
	private ServisRepository servisRepository;
	
	@RequestMapping(value={"/admin/servis/", "/admin/servis/lista"}, method = RequestMethod.GET)
	public String getSviServisi(Model model, HttpServletRequest request){

		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", servisRepository.findAll(pageable));
		return "/admin/servis/dio/lista";
	}
	
	@RequestMapping(value="/admin/servis/zavrseni", method = RequestMethod.GET)
	public String getSviUradjeniServisi(Model model, HttpServletRequest request){

		int page;
		if(request.getParameter("page")==null){
			page=0;
		} else{
			page = Integer.parseInt(request.getParameter("page"));
		}

		int pageSize = 4;

		Pageable pageable = new PageRequest(page, pageSize);
		model.addAttribute("pager", servisRepository.findByZavrsenTrue(pageable));
		return "/admin/servis/dio/lista";
	}

}

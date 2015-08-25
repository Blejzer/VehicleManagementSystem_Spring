package ba.fit.vms.controllers;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.SessionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Tiket2;
import ba.fit.vms.repository.KorisnikRepository;
import ba.fit.vms.repository.KorisnikVoziloRepository;
import ba.fit.vms.repository.PorukaRepository;
import ba.fit.vms.repository.Tiket2Repository;

@Controller
public class PorukaController {
	
	@Autowired
	private PorukaRepository pRepository;
	
	@Autowired
	private Tiket2Repository tRepository;
	
	@Autowired
	private KorisnikRepository korisnikRepository;	
	
	@Autowired
	private KorisnikVoziloRepository kvRepository;
	
	
	@RequestMapping(value={"/korisnik/{kid}/tiket/{tid}/novi"}, method = RequestMethod.GET)
	public String getNovaPoruka(@PathVariable("kid") Long kid, @PathVariable("tid") Long tid, Principal principal, Model model){
		
		Korisnik k;
		if(kid!=null){
			k = korisnikRepository.findOne(kid);
		}else{
			k = korisnikRepository.find(principal.getName());
		}
		System.out.println(k.getEmail());
		
		Tiket2 t2;
		if(tid!=null){
			t2 = tRepository.findOne(tid);
		}else{
			System.out.println("nema tiketa sa tim brojem");
			return "redirect:/korisnik/"+kid+"/tiketi/novi";
		}
		System.out.println(t2.getId());
		
		
		return null;
	}
	
	
	@RequestMapping(value={"/korisnik/{kid}/tiket/{tid}/poruke"}, method = RequestMethod.GET)
	public String getListaPoruka(@PathVariable("kid") Long kid, @PathVariable("tid") Long tid, Principal principal, HttpServletRequest request, Model model){
		KorisnikVozilo kv;
		if(kid!=null){
			kv = kvRepository.findByKorisnik_EmailAndVracenoNull(korisnikRepository.findOne(kid).getEmail());
		}else{
			kv = kvRepository.findByKorisnik_EmailAndVracenoNull(principal.getName());
		}
		System.out.println(kv.getKorisnik().getEmail());
		
		Tiket2 t2;
		if(tid!=null){
			t2 = tRepository.findOne(tid);
		}else{
			System.out.println("nema tiketa sa tim brojem");
			return "redirect:/korisnik/"+kid+"/tiketi/novi";
		}
		System.out.println(t2.getId());
		
		if(request.getParameter("page")==null)
		{
			PagedListHolder poruke = new PagedListHolder(t2.getPoruke());
			poruke.setPageSize(4);
			request.getSession().setAttribute("Tiket2Controller_poruke", poruke);
			model.addAttribute("tAtribut", t2);
			model.addAttribute("pager", poruke);
			model.addAttribute("kvAtribut", kv);
			
			return "/korisnik/tiket/poruka/lista";
		}
		else 
		{
			String page = request.getParameter("page");
			System.out.println("else petlja");
			PagedListHolder poruke = (PagedListHolder) request.getSession().getAttribute("Tiket2Controller_poruke");
			if (poruke == null) 
			{
				throw new SessionException("Vasa sesija je istekla, molimo pokusajte ponoviti pretragu");
			}
			else
			{
				poruke.setPage(Integer.parseInt(page));
				model.addAttribute("tAtribut", t2);
				model.addAttribute("pager", poruke);
				model.addAttribute("kvAtribut", kv);
			}
			return "/korisnik/tiket/poruka/lista";
		}
		
	}

}

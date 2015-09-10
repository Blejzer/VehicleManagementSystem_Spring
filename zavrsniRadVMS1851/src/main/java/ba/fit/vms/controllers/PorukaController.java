package ba.fit.vms.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.hibernate.SessionException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Poruka;
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
		
		KorisnikVozilo kv;
		if(kid!=null){
			kv = kvRepository.findByKorisnik_EmailAndVracenoNull(korisnikRepository.findOne(kid).getEmail());
		}else{
			kv = kvRepository.findByKorisnik_EmailAndVracenoNull(principal.getName());
		}
		System.out.println("GET: "+kv.getKorisnik().getEmail());
		
		Tiket2 t2;
		if(tid!=null){
			t2 = tRepository.findOne(tid);
		}else{
			System.out.println("GET: nema tiketa sa tim brojem");
			return "redirect:/korisnik/"+kid+"/tiketi/novi";
		}
		System.out.println("GET: "+t2.getId());

		System.out.println("GET: "+t2.getPoruke().size());
		
		Poruka slijedeca = new Poruka();
		
		slijedeca.setKorisnik(kv.getKorisnik());
		slijedeca.setDatum(new DateTime().toDate());
		if(!t2.getPoruke().isEmpty()){
			slijedeca.setPrethodni(t2.getPoruke().get(t2.getPoruke().size()-1));
		}
		model.addAttribute("pAtribut", slijedeca);
		model.addAttribute("tAtribut", t2);
		
		System.out.println("GET: tiket2 id = "+t2.getId());
		System.out.println("GET: poruka id = "+slijedeca.getId());
		System.out.println("GET: prethodna id = "+slijedeca.getPrethodni().getId());
			
		return "/korisnik/tiket/poruka/novi";
	}
	
	
	@RequestMapping(value={"/korisnik/{kid}/tiket/{tid}/novi"}, method = RequestMethod.POST)
	public String postNovaPoruka(@PathVariable("kid") Long kid, @PathVariable("tid") Long tid, @ModelAttribute("pAtribut") @Valid Poruka poruka, HttpServletRequest request,  BindingResult porukaRezultat, Principal principal, Model model ){
	
		System.out.println("POST: prethodni id = "+poruka.getPrethodni().getId());
		System.out.println("POST: poruka id = "+poruka.getId());
		
		if(porukaRezultat.hasErrors()){
			System.out.println("POST: poruka greske: " + porukaRezultat.toString());
			model.addAttribute("tAtribut", tRepository.findOne(tid));

			return "/korisnik/tiket/poruka/novi";
		}
		try {
			Tiket2 t2 = tRepository.findOne(tid);
			List<Poruka> poruke = new ArrayList<Poruka>();
			poruke = t2.getPoruke();
			poruke.add(poruka);
			t2.setPoruke(poruke);
			System.out.println("POST: "+t2.getPoruke().size());
			tRepository.saveAndFlush(t2);
		} catch (Exception e) {
			System.out.println("POST: Exception greska: "+e.getMessage());
			System.out.println("POST: Exception cause: "+e.getCause());
			String link = "redirect:/korisnik/"+kid.toString()+"/tiketi/";
			return link;
			
		}
		String link = "redirect:/korisnik/"+kid+"/tiket/"+tid+"/poruka";
		if(request.getParameter("page")!=null){
			link = link.concat("?page="+request.getParameter("page"));
		}
		
		System.out.println("POST: "+link);
		
		return link;
	}
	
	
	
	@RequestMapping(value={"/korisnik/{kid}/tiket/{tid}/poruka"}, method = RequestMethod.GET)
	public String getListPoruka(@PathVariable("kid") Long kid, @PathVariable("tid") Long tid, Principal principal, HttpServletRequest request, Model model){
		
		KorisnikVozilo kv = kvRepository.findByKorisnik_EmailAndVracenoNull(korisnikRepository.findOne(kid).getEmail());
		Korisnik k = korisnikRepository.find(principal.getName());
		
		Tiket2 t2;
		if(tid!=null){
			t2 = tRepository.findOne(tid);
		}else{
			System.out.println("nema tiketa sa tim brojem");
			return "redirect:/korisnik/"+kid+"/tiketi/novi";
		}
		System.out.println(t2.getId());
		Poruka slijedeca = new Poruka();
		slijedeca.setDatum(new DateTime().toDate());
		slijedeca.setKorisnik(k);
		String link = "/korisnik/tiket/poruka/list";
		if(request.getParameter("page")==null)
		{
			List<Poruka> lista = t2.getPoruke();
			Collections.sort(lista, Collections.reverseOrder());
			PagedListHolder<Poruka> poruke = new PagedListHolder<Poruka>(lista);
			poruke.setPageSize(10);
			request.getSession().setAttribute("Tiket2Controller_poruke", poruke);
			model.addAttribute("tAtribut", t2);
			model.addAttribute("pager", poruke);
			model.addAttribute("kvAtribut", kv);
			
			if(poruke.isFirstPage()){
				slijedeca.setPrethodni(poruke.getPageList().get(poruke.getPageList().size()-1));
				model.addAttribute("pAtribut", slijedeca);
			}
		}
		else 
		{
			String page = request.getParameter("page");
			//System.out.println("else petlja");
			
			@SuppressWarnings("unchecked")
			PagedListHolder<Poruka> poruke = (PagedListHolder<Poruka>) request.getSession().getAttribute("Tiket2Controller_poruke");
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
				if(poruke.isFirstPage()){
					slijedeca.setPrethodni(poruke.getPageList().get(poruke.getPageList().size()-1));
					model.addAttribute("pAtribut", slijedeca);
				}
			}
		}
			return link;
		
	}
	

	
	@RequestMapping(value={"/korisnik/{kid}/tiket/{tid}/zatvori"}, method = RequestMethod.POST)
	public String postZatvoriTiket(@PathVariable("kid") Long kid, @PathVariable("tid") Long tid, Principal principal, HttpServletRequest request, Model model){
		
		Tiket2 t2;
		if(tid!=null){
			t2 = tRepository.findOne(tid);
			t2.setRijesenDatum(new DateTime().toDate());
			tRepository.saveAndFlush(t2);
		}else{
			System.out.println("nema tiketa sa tim brojem");
			return "redirect:/korisnik/"+kid+"/tiketi/";
		}
		System.out.println("Zatvori.t2.brojPoruka: "+tRepository.findOne(t2.getId()).getPoruke().size());
		System.out.println("redirect:/korisnik/"+kid+"/tiketi/");
		
		return "redirect:/korisnik/"+kid.toString()+"/tiketi/";
		
	}

}

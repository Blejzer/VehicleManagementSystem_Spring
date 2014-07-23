package ba.fit.vms.pretraga;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.repository.KorisnikRepository;

public class VoziloPretraga {
	
	@Autowired
	private KorisnikRepository korisnikRepository;
	
	private String vin;
	private String tablice;
	
	public List<Korisnik> listaKorisnika;

}

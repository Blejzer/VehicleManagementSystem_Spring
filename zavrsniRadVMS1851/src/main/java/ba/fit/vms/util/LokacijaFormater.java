package ba.fit.vms.util;

import java.text.ParseException;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.format.Formatter;

import ba.fit.vms.pojo.Lokacija;
import ba.fit.vms.repository.LokacijaRepository;

public class LokacijaFormater implements Formatter<Lokacija>{
	/** String koji predstavlja null. */
	private static final String NULL_REPRESENTATION = "null";
	
	@Resource
	private LokacijaRepository lokacijaRepository;
	
	public LokacijaFormater() {
		super();
	}

	@Override
	public String print(Lokacija lokacija, Locale locale) {
		if(lokacija.equals(NULL_REPRESENTATION)){
			return null;
		}
		try {
			Lokacija novaLokacija = new Lokacija();
			novaLokacija.setId(lokacija.getId());
			return novaLokacija.getId().toString();
		} catch (NumberFormatException e) {
			throw new RuntimeException("Nisam uspio konvertovati `" + lokacija + "` u validan id");
		}
		
	}

	@Override
	public Lokacija parse(String text, Locale locale) throws ParseException {
		if (text.equals(NULL_REPRESENTATION)) {
			return null;
		}
		try {
			Long id = Long.parseLong(text);
			Lokacija lokacija = new Lokacija();
			lokacija.setId(id);
			return lokacija;
		} catch (NumberFormatException e) {
			throw new RuntimeException("Nisam uspio konvertovati `" + text + "` u validnu Lokaciju");
		}
		
	}

}

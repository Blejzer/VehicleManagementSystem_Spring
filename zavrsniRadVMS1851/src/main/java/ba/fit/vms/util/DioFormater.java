package ba.fit.vms.util;

import java.text.ParseException;
import java.util.Locale;

import javax.annotation.Resource;

import org.springframework.format.Formatter;

import ba.fit.vms.pojo.Dio;
import ba.fit.vms.repository.DioRepository;

public class DioFormater implements Formatter<Dio>{
	
	/** String koji predstavlja null. */
	private static final String NULL_REPRESENTATION = "null";
	
	@Resource
	private DioRepository dioRepository;
	
	

	public DioFormater() {
		super();
	}

	@Override
	public String print(Dio dio, Locale locale) {
		if(dio.equals(NULL_REPRESENTATION)){
			return null;
		}
		try {
			Dio noviDio = new Dio();
			noviDio.setId(dio.getId());
			return noviDio.getId().toString();
		} catch (NumberFormatException e) {
			throw new RuntimeException("Nisam uspio konvertovati `" + dio + "` u validan id");
		}
		
	}

	@Override
	public Dio parse(String text, Locale locale) throws ParseException {
		if (text.equals(NULL_REPRESENTATION)) {
			return null;
		}
		try {
			Long id = Long.parseLong(text);
			Dio dio = new Dio();
			dio.setId(id);
			return dio;
		} catch (NumberFormatException e) {
			throw new RuntimeException("Nisam uspio konvertovati `" + text + "` u validan Dio");
		}
		
	}
	

}

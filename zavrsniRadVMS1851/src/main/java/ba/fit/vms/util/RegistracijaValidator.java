package ba.fit.vms.util;

import javax.persistence.NoResultException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.VoziloRepository;
@Component("registracijaValidator")
public class RegistracijaValidator implements Validator {

	protected static Logger logger = Logger.getLogger("repo");

	@Autowired
	private RegistracijaRepository registracijaRepository;
	
	@Autowired
	private VoziloRepository voziloRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return Registracija.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Registracija registracija = (Registracija) target;
		logger.debug("poceo validate za " + registracija.getVozilo().getVin());
		logger.debug("pokusavam naci vozilo: " + registracija.getVozilo().getVin());
		Vozilo vozilo1 = voziloRepository.findOne(registracija.getVozilo().getVin());
		logger.debug(vozilo1.getVin());
		Vozilo vozilo = voziloRepository.findOne(registracija.getVozilo().getVin());
		logger.debug(vozilo.getVin());
		registracija.setVozilo(vozilo);

		logger.debug("poceo validate za " + registracija.getVozilo().getVin());

		Registracija aktivna = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(registracija.getVozilo().getVin());
		logger.debug("provjerio u bazi");
		if(aktivna==null){
			logger.debug("nema ga, provjeramo jeAktivno");
			if(!(registracija.getJeAktivno())){
				errors.rejectValue("jeAktivno", "jeAktivno.alreadyExist",
						"Prva registracija vozila mora biti aktivna!");
			}

		}
	}

	public Registracija obrada(Registracija registracija, Errors errors){
		Registracija aktivna = new Registracija();
		String vin = "";
		try {
			vin = registracija.getVozilo().getVin();
			logger.debug("pokupio vin, pokusavam naci aktivnu");
			aktivna = registracijaRepository.findByVozilo_VinAndJeAktivnoTrue(vin);
			if(aktivna !=null){
				logger.debug("nasao");
				if (registracija.getJeAktivno()) {
					if(registracija.getRegOd().after(aktivna.getRegOd())){
						if(registracija.getOsigOd().after(aktivna.getOsigOd())){
							aktivna.setJeAktivno(false);
							registracijaRepository.save(aktivna);
							return registracija;
						}else{
							errors.rejectValue("osigOd", "osigOd.pogresanDatum",
									"Datumi osiguranja ne odgovaraju!");
							return registracija;
						}

					}else{
						errors.rejectValue("regOd", "regOd.pogresanDatum",
								"Datumi registracije ne odgovaraju!");
						return registracija;
					}
				} else{
					if(registracija.getRegOd().after(aktivna.getRegOd()) || registracija.getOsigOd().after(aktivna.getOsigOd())){
						errors.rejectValue("osigOd", "osigOd.alreadyExists",
								"Datumi osiguranja ne odgovaraju!");
						return registracija;
					}else{
						return registracija;
					}

				}

			}else{
				logger.debug("nisam nasao");
				if(!(registracija.getJeAktivno())){
					errors.rejectValue("jeAktivno", "jeAktivno.alreadyExist",
							"Prva registracija vozila mora biti aktivna!");
					return registracija;
				} else {
					return registracija;
				}
			}
		}catch(NoResultException nre){
			logger.debug(nre.getMessage());
			return registracija;
		}
			
	}
		

}

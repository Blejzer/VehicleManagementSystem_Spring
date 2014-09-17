package ba.fit.vms.util;

import javax.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.repository.RegistracijaRepository;
@Component("registracijaValidator")
public class RegistracijaValidator implements Validator {

	@Autowired
	private RegistracijaRepository registracijaRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return Registracija.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Registracija registracija = (Registracija) target;
		try {
			@SuppressWarnings("unused")
			Registracija aktivna = registracijaRepository.findActiveRegForVozilo(registracija.getVozilo().getVin());
		} catch (NoResultException nre) {
			if(!(registracija.getJeAktivno())){
				errors.rejectValue("jeAktivno", "jeAktivno.alreadyExist",
						"Prva registracija vozila mora biti aktivna!");
			}

		}
	}

	public Registracija obrada(Object target, Errors errors){
		Registracija reg = (Registracija) target;
		Registracija aktivna = new Registracija();
		String vin = "";
		try {
			vin = reg.getVozilo().getVin();
			aktivna = registracijaRepository.findActiveRegForVozilo(vin);
			if (reg.getJeAktivno()) {
				if(reg.getRegOd().after(aktivna.getRegOd())){
					if(reg.getOsigOd().after(aktivna.getOsigOd())){
						aktivna.setJeAktivno(false);
						registracijaRepository.save(aktivna);
						return reg;
					}else{
						errors.rejectValue("osigOd", "osigOd.pogresanDatum",
								"Datumi osiguranja ne odgovaraju!");
						return reg;
					}

				}else{
					errors.rejectValue("regOd", "regOd.pogresanDatum",
							"Datumi registracije ne odgovaraju!");
					return reg;
				}
			} else{
				if(reg.getRegOd().after(aktivna.getRegOd()) || reg.getOsigOd().after(aktivna.getOsigOd())){
					errors.rejectValue("osigOd", "osigOd.alreadyExists",
							"Datumi osiguranja ne odgovaraju!");
					return reg;
				}else{
					return reg;
				}

			}

		}catch (NoResultException nre) {
			if(!(reg.getJeAktivno())){
				errors.rejectValue("jeAktivno", "jeAktivno.alreadyExist",
						"Prva registracija vozila mora biti aktivna!");
				return reg;
			} else {
				return reg;
			}
		}
	}

}

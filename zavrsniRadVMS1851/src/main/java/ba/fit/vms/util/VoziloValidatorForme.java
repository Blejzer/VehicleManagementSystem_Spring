package ba.fit.vms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.VoziloRepository;

@Component("voziloValidatorForme")
public class VoziloValidatorForme implements Validator {

	@Autowired
	private VoziloRepository voziloRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return Vozilo.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		Vozilo vozilo = (Vozilo)target;

		if(!vozilo.getVin().isEmpty()){
			String vin = vozilo.getVin();

			if(voziloRepository.exists(vin)){
				errors.rejectValue("vin", "vin.alreadyExist",
	                    "Vozilo sa ovim VIN brojem vec postoji!");
	        }
		}


	}

}

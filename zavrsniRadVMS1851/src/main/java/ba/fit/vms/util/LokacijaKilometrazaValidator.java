package ba.fit.vms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ba.fit.vms.pojo.LokacijaKilometraza;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;

@Component("lkValidator")
public class LokacijaKilometrazaValidator implements Validator{

	@Autowired
	private LokacijaKilometrazaRepository lkRepository;
	@Override
	public boolean supports(Class<?> clazz) {
		return LokacijaKilometraza.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		LokacijaKilometraza lk = (LokacijaKilometraza)target;
		
		String vin = lk.getKorisnikVozilo().getVozilo().getVin();
		System.out.println("pronasao vin vozila: " + vin);
		LokacijaKilometraza max = lkRepository.getMaxMileage(vin);
		if(max!=null){
			System.out.println("pronasao maks kilometrazu: "+ max.getKilometraza());
			if(max.getKilometraza()>=lk.getKilometraza() & max.getDatum().compareTo(lk.getDatum())<0){
				errors.rejectValue("kilometraza", "kilometraza.mustBeGreater",
						"Ukoliko je uneseno zadnje stanje kilometraze, kilometraza mora biti veca od zadnje unesene!");
			} else{
				if((max.getKilometraza()<=lk.getKilometraza() & max.getDatum().compareTo(lk.getDatum())>=0)){
					errors.rejectValue("datum", "datum.mustBeLess",
							"Ukoliko uneseno stanje kilometraze nije zadnje, datum unosa mora biti manji od zadnjeg!");
				}
				
			}
		}
		
		
	}

}

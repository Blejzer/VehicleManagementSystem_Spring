package ba.fit.vms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ba.fit.vms.pojo.LokacijaKilometraza;
import ba.fit.vms.repository.LokacijaKilometrazaRepository;

@Component("lkValidator")
public class LokacijaKilometrazaValidator implements Validator{
	
	// Spring Message Source potreban da bi mogli raditi sa porukama
	@Autowired
	private MessageSource messageSource; 

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
		
		LokacijaKilometraza prethodna = new LokacijaKilometraza();
		LokacijaKilometraza naredna = new LokacijaKilometraza();

		// Provjeravamo da li postoji prethodno unesena kilometraza
		try {
			prethodna = lkRepository.getMaxKilo2(vin, lk.getDatum());
			System.out.println("prethodna je: "+ prethodna.getKilometraza());
		} catch (Exception e) {
			System.out.println("prethodna: "+e.getMessage());
			prethodna = new LokacijaKilometraza();
			prethodna.setId(Long.valueOf(0));
		}

		// Provjeravamo da li postoji naknadno unesena kilometraza
		try {
			naredna = lkRepository.getMaxKilo3(vin, lk.getDatum());
			System.out.println("naredna je: "+ naredna.getKilometraza());
		} catch (NullPointerException e) {
			System.out.println("naredna: " + e.getMessage());
			naredna = new LokacijaKilometraza();
			naredna.setId(Long.valueOf(0));
			System.out.println("dodao nulu: ");
		}
		
		// Ukoliko se dodaje kilometraza koja nije prva niti posljednja
		if(naredna.getId()!=0 && prethodna.getId()!=0){
			System.out.println("srednja: start if");
			if(naredna.getKilometraza()<=lk.getKilometraza()){
				String[] args = new String[] {naredna.getKilometraza().toString()};
				try {
					errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeGreater", args, null);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeGreater");
				}
			}else {
				if(prethodna.getKilometraza()>=lk.getKilometraza()){
					String[] args = new String[] {prethodna.getKilometraza().toString()};
					try {
						errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeLesser", args, null);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeLesser");
					}
					
				}
				System.out.println("srednja: end if");
			}
		}else{
			// Ukoliko se dodaje posljednja kilometraza ili prva kilometraza
			if(naredna.getId()!=0){
				System.out.println("naredna: start if");
				if(naredna.getKilometraza()<=lk.getKilometraza()){
					String[] args = new String[] {naredna.getKilometraza().toString()};
					try {
						errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeGreater", args, null);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeGreater");
					}
					System.out.println("naredna: end if");
				}else {
					if (prethodna.getId()!=0) {
						System.out.println("prethodna: start if" + prethodna.getKilometraza());
						if(prethodna.getKilometraza()>=lk.getKilometraza()){
							String[] args = new String[] {prethodna.getKilometraza().toString()};
							try {
								errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeLesser", args, null);
							} catch (Exception e) {
								System.out.println(e.getMessage());
								errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeLesser");
							}
							
						}
						System.out.println("prethodna: end if");
					}
				}
				
			}else if (prethodna.getId()!=0) {
				System.out.println("prethodna: start if");
				if(prethodna.getKilometraza()>=lk.getKilometraza()){
					String[] args = new String[] {prethodna.getKilometraza().toString()};
					try {
						errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeLesser", args, null);
					} catch (Exception e) {
						System.out.println(e.getMessage());
						errors.rejectValue("kilometraza", "kilometraza.lkAtribut.kilometraza.mustBeLesser");
					}
					
				}
				System.out.println("prethodna: end if");
			}
		}
		// END 


	}

}

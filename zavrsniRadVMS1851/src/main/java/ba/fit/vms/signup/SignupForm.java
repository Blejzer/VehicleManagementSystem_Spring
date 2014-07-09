package ba.fit.vms.signup;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import ba.fit.vms.pojo.Korisnik;

public class SignupForm {
	
	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	@Email(message = SignupForm.EMAIL_MESSAGE)
	private String email;
    
    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String ime;
	
    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String prezime;
	
	@NotNull(message = SignupForm.NOT_BLANK_MESSAGE)
	private String jeAktivan;

    @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String lozinka;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIme() {
		return ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public String getJeAktivan() {
		return jeAktivan;
	}

	public void setJeAktivan(String jeAktivan) {
		this.jeAktivan = jeAktivan;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}
    
    public Korisnik kreirajKorisnika(){
    	Boolean jeAktivan = null;
    	if("true".equals(getJeAktivan())){
    		jeAktivan = true;
    	} else if("false".equals(getJeAktivan())){
    		jeAktivan = false;
    	}
    	
    	return new Korisnik(getEmail(), getIme(), getPrezime(), jeAktivan, getLozinka(), "ROLE_USER");
    }

}

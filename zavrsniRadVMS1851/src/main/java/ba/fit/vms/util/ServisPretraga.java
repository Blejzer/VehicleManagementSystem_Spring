package ba.fit.vms.util;

import java.util.LinkedHashMap;
import java.util.List;

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.repository.RegistracijaRepository;

public class ServisPretraga {

	private String vin;
	private Integer mjesec;
	private Integer godina;

	private List<Registracija> registracije;
	private LinkedHashMap<Integer, String> mjeseci;
	private LinkedHashMap<Integer, Integer> godine;

	public ServisPretraga(RegistracijaRepository  registracijaRepository){
		DateTimeGenerator now = new DateTimeGenerator();
		this.registracije = registracijaRepository.findAllByJeAktivnoTrueOrderByRegDoDesc();
		this.mjeseci = now.getMonths();
		this.godine = now.getYears();
		this.mjesec = now.getMonth();
		this.godina = now.getYear();
	}
	public ServisPretraga(){};


	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public Integer getMjesec() {
		return mjesec;
	}
	public void setMjesec(Integer mjesec) {
		this.mjesec = mjesec;
	}
	public Integer getGodina() {
		return godina;
	}
	public void setGodina(Integer godina) {
		this.godina = godina;
	}
	public List<Registracija> getRegistracije() {
		return registracije;
	}
	public void setRegistracije(List<Registracija> registracije) {
		this.registracije = registracije;
	}
	public LinkedHashMap<Integer, String> getMjeseci() {
		return mjeseci;
	}
	public void setMjeseci(LinkedHashMap<Integer, String> mjeseci) {
		this.mjeseci = mjeseci;
	}
	public LinkedHashMap<Integer, Integer> getGodine() {
		return godine;
	}
	public void setGodine(LinkedHashMap<Integer, Integer> godine) {
		this.godine = godine;
	}
}

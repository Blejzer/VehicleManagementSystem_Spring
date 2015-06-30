package ba.fit.vms.util;

import java.util.LinkedHashMap;
import java.util.List;

import ba.fit.vms.pojo.Registracija;
import ba.fit.vms.pojo.Vozilo;
import ba.fit.vms.repository.RegistracijaRepository;
import ba.fit.vms.repository.VoziloRepository;

public class KilometrazaPretraga {
	
	private String vin;
	private Integer mjesec;
	private Integer godina;
	
	private List<Registracija> registracije;
	private List<Vozilo> vozila;
	private LinkedHashMap<Integer, String> mjeseci;
	private LinkedHashMap<Integer, Integer> godine;
	
	public KilometrazaPretraga(VoziloRepository voziloRepository){
		
		DateTimeGenerator now = new DateTimeGenerator();
		this.vozila = voziloRepository.findAll();
		this.mjeseci = now.getMonths();
		this.godine = now.getYears();
		this.mjesec = now.getMonth();
		this.godina = now.getYear();		
	}
public KilometrazaPretraga(RegistracijaRepository regRepository){
		
		DateTimeGenerator now = new DateTimeGenerator();
		this.registracije = regRepository.findAllByJeAktivnoTrueOrderByRegDoDesc();
		this.mjeseci = now.getMonths();
		this.godine = now.getYears();
		this.mjesec = now.getMonth();
		this.godina = now.getYear();		
	}
	
	public KilometrazaPretraga(){}

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

	public List<Vozilo> getVozila() {
		return vozila;
	}

	public void setVozila(List<Vozilo> vozila) {
		this.vozila = vozila;
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
	public List<Registracija> getRegistracije() {
		return registracije;
	}
	public void setRegistracije(List<Registracija> registracije) {
		this.registracije = registracije;
	};

}

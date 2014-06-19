package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="kilometraza")
public class Kilometraza implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="kilometri")
	@NotNull(message= "Trenutna kilometraza ne moze biti prazna")
	private Long mileage;
	
	@Column(name = "datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date datum;
	
	@ManyToOne( cascade = {CascadeType.REMOVE}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	@NotNull
	private Vozilo vozilo;
	
	@ManyToOne( cascade = {CascadeType.REMOVE}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false, updatable=false)
	@NotNull
	private Korisnik korisnik;

	//***********************************************
	//*  			Getteri i Setteri 				*
	//*                    							*
	// **********************************************
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMileage() {
		return mileage;
	}

	public void setMileage(Long mileage) {
		this.mileage = mileage;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public Vozilo getVozilo() {
		return vozilo;
	}

	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}

	public Korisnik getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnik korisnik) {
		this.korisnik = korisnik;
	}
}

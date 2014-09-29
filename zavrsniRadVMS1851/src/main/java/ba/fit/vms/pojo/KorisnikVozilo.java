package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="korisnik_vozilo")
public class KorisnikVozilo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	
	@ManyToOne( cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(name="vozilo_vin", nullable=false)
	private Vozilo vozilo;
	
	@ManyToOne( cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(name="korisnik_id", nullable=false)
	private Korisnik korisnik;
	
	@Column(name = "dodijeljeno")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date dodijeljeno;
	
	@Column(name = "vraceno")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date vraceno;
	
	
	/************************************************
	/*  			Getteri i Setteri 				*
	/*                    							*
	/***********************************************/
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Date getDodijeljeno() {
		return dodijeljeno;
	}

	public void setDodijeljeno(Date dodijeljeno) {
		this.dodijeljeno = dodijeljeno;
	}

	public Date getVraceno() {
		return vraceno;
	}

	public void setVraceno(Date vraceno) {
		this.vraceno = vraceno;
	}

}

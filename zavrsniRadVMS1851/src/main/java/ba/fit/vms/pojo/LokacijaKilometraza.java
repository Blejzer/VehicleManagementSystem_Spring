package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
@Table(name="lokacija_kilometraza")
public class LokacijaKilometraza implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Long id;

	@Column(name="kilometraza")
	@NotNull
	private Long kilometraza;
	
	@ManyToOne( cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false, updatable=true)
	private Lokacija lokacija;
	
	@ManyToOne( cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=true, updatable=false)
	private KorisnikVozilo korisnikVozilo;
	
	@Column(name = "datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date datum;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getKilometraza() {
		return kilometraza;
	}

	public void setKilometraza(Long kilometraza) {
		this.kilometraza = kilometraza;
	}

	public Lokacija getLokacija() {
		return lokacija;
	}

	public void setLokacija(Lokacija lokacija) {
		this.lokacija = lokacija;
	}

	public KorisnikVozilo getKorisnikVozilo() {
		return korisnikVozilo;
	}

	public void setKorisnikVozilo(KorisnikVozilo korisnikVozilo) {
		this.korisnikVozilo = korisnikVozilo;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
		return false;
		}
		if (getClass() != obj.getClass()) {
		return false;
		}
		final LokacijaKilometraza other = (LokacijaKilometraza) obj;
		if (!Objects.equals(this.id, other.id)) {
		return false;
		}
		return true;
		}
	
	

}

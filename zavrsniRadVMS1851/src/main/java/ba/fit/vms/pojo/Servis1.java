package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="servis1")
public class Servis1 implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date datum;
	
	@ManyToOne( cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	@NotNull
	private Vozilo vozilo;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinTable(name = "servis_dio", joinColumns = { 
			@JoinColumn(name = "servis_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "dio_id", 
					nullable = false, updatable = false) })
	private List<Dio> djelovi = new ArrayList<Dio>();
	
	@ManyToOne( cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	@NotNull
	private VrstaServisa vrstaServisa;
	
	@ManyToOne( cascade = {CascadeType.PERSIST}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	@NotNull
	private LokacijaKilometraza lokacijaKilometraza;
	
	private Boolean zavrsen;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<Dio> getDjelovi() {
		return djelovi;
	}

	public void setDjelovi(List<Dio> djelovi) {
		this.djelovi = djelovi;
	}

	public VrstaServisa getVrstaServisa() {
		return vrstaServisa;
	}

	public void setVrstaServisa(VrstaServisa vrstaServisa) {
		this.vrstaServisa = vrstaServisa;
	}

	public LokacijaKilometraza getLokacijaKilometraza() {
		return lokacijaKilometraza;
	}

	public void setLokacijaKilometraza(LokacijaKilometraza lokacijaKilometraza) {
		this.lokacijaKilometraza = lokacijaKilometraza;
	}

	public Boolean getZavrsen() {
		return zavrsen;
	}

	public void setZavrsen(Boolean zavrsen) {
		this.zavrsen = zavrsen;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
		return false;
		}
		if (getClass() != obj.getClass()) {
		return false;
		}
		final Servis1 other = (Servis1) obj;
		if (!Objects.equals(this.id, other.id)) {
		return false;
		}
		return true;
		}
}

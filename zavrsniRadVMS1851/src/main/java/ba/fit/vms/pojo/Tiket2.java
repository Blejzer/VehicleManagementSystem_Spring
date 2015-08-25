package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "tiket2")
public class Tiket2 implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name="naslov")
	@NotEmpty(message= "Morate unijeti naslov tiketa")
	private String naslov;
	
	@Column(name = "tiket_datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private Date tiketDatum;
	
	@Column(name = "rijesen_datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date rijesenDatum;
	
	@ManyToOne(cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	@NotNull
	private Korisnik korisnik;
	
	@ManyToOne(cascade = {CascadeType.REFRESH}, fetch=FetchType.EAGER )
	@JoinColumn(name="vozilo_vin")
	private Vozilo vozilo;
	
	/*@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
	@JoinTable(name = "tiket2_poruka", joinColumns = { 
			@JoinColumn(name = "tiket2_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "poruka_id", 
					nullable = false, updatable = false) })
	@GenericGenerator(name="uuid-gen", strategy = "increment")
    @CollectionId(columns = @Column(name = "collection_id"), generator = "uuid-gen", type = @Type(type = "long"))
    
    Hibernate problem kod kreiranja veza XxxToMany */
	
	@OneToMany(cascade = {CascadeType.ALL}, fetch=FetchType.EAGER )
	@GenericGenerator(name="uuid-gen", strategy = "increment") 
    @CollectionId(columns = @Column(name = "collection_id"), generator = "uuid-gen", type = @Type(type = "long"))
	private List<Poruka> poruke = new ArrayList<Poruka>();
	
	//
	// GETTERS AND SETTERS
	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNaslov() {
		return naslov;
	}

	public void setNaslov(String naslov) {
		this.naslov = naslov;
	}

	public Date getTiketDatum() {
		return tiketDatum;
	}

	public void setTiketDatum(Date tiketDatum) {
		this.tiketDatum = tiketDatum;
	}

	public Date getRijesenDatum() {
		return rijesenDatum;
	}

	public void setRijesenDatum(Date rijesenDatum) {
		this.rijesenDatum = rijesenDatum;
	}

	public Korisnik getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnik korisnik) {
		this.korisnik = korisnik;
	}

	public Vozilo getVozilo() {
		return vozilo;
	}

	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}

	public List<Poruka> getPoruke() {
		return poruke;
	}

	public void setPoruke(List<Poruka> poruke) {
		this.poruke = poruke;
	}

}

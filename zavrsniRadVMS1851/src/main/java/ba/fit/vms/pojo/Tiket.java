package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "tiket")
public class Tiket implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name="naslov")
	@NotNull(message= "Morate unijeti naslov tiketa")
	private String naslov;

	@Column(name="sadrzaj")
	@NotNull(message= "Sadrzaj ne moze biti prazan")
	private String sadrzaj;

	@Column(name="odgovor")
	private String odgovor;
	
	@Column(name = "tiket_datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date tiketDatum;
	
	@Column(name = "rijesen_datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date rijesenDatum;
	
	@Column(name = "rijesen", nullable=false)
	private Boolean jeRrijesen;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "korisnik_id", nullable = true)
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

	public String getNaslov() {
		return naslov;
	}

	public void setNaslov(String naslov) {
		this.naslov = naslov;
	}

	public String getSadrzaj() {
		return sadrzaj;
	}

	public void setSadrzaj(String sadrzaj) {
		this.sadrzaj = sadrzaj;
	}

	public String getOdgovor() {
		return odgovor;
	}

	public void setOdgovor(String odgovor) {
		this.odgovor = odgovor;
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

	public Boolean getJeRrijesen() {
		return jeRrijesen;
	}

	public void setJeRrijesen(Boolean jeRrijesen) {
		this.jeRrijesen = jeRrijesen;
	}

	public Korisnik getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnik korisnik) {
		this.korisnik = korisnik;
	}
	
	

}

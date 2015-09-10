package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "poruka")
public class Poruka  implements Serializable, Comparable<Poruka>{ //
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Lob
	@Column(name="sadrzaj", columnDefinition="TEXT")
	@NotEmpty(message= "Sadrzaj ne moze biti prazan")
	private String sadrzaj;
	
	@Column(name = "datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private Date datum;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="korisnik_id", nullable=true)
	private Korisnik korisnik;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="prethodna_id", nullable=true)
	private Poruka prethodni;

	@Override
	public int compareTo(Poruka o) {
		return this.datum.compareTo(o.datum);
	}
	
	
	//
	// GETTERS AND SETTERS
	//

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSadrzaj() {
		return sadrzaj;
	}

	public void setSadrzaj(String sadrzaj) {
		this.sadrzaj = sadrzaj;
	}

	public Date getDatum() {
		return datum;
	}

	public void setDatum(Date datum) {
		this.datum = datum;
	}

	public Korisnik getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnik korisnik) {
		this.korisnik = korisnik;
	}

	public Poruka getPrethodni() {
		return prethodni;
	}

	public void setPrethodni(Poruka prethodni) {
		this.prethodni = prethodni;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
		return false;
		}
		if (getClass() != obj.getClass()) {
		return false;
		}
		final Poruka other = (Poruka) obj;
		if (!Objects.equals(this.id, other.id)) {
		return false;
		}
		return true;
		}
}

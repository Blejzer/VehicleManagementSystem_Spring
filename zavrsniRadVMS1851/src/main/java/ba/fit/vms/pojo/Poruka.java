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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "poruka")
public class Poruka  implements Serializable{ //, Comparable<Poruka>
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="sadrzaj")
	@NotNull(message= "Sadrzaj ne moze biti prazan")
	private String sadrzaj;
	
	@Column(name = "datum")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull
	private Date datum;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="prethodna_id", nullable=true)
	private Poruka prethodni;
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="naredna_id", nullable=true)
	private Poruka naredna;
	
	/*@ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
	@JoinColumn(name = "tiket2_id", nullable = false)
	private Tiket2 tiket2;*/

	/*@Override
	public int compareTo(Poruka o) {
		return this.id.compareTo(o.id);
	}*/
	
	
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

	public Poruka getPrethodni() {
		return prethodni;
	}

	public void setPrethodni(Poruka prethodni) {
		this.prethodni = prethodni;
	}


	public Poruka getNaredna() {
		return naredna;
	}


	public void setNaredna(Poruka naredna) {
		this.naredna = naredna;
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

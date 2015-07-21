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
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "registracija")
public class Registracija implements Serializable, Comparable<Registracija> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;

	@Column(name="tablica")
	@Pattern(regexp="^[AEJKMO]{1}[0-9]{2}-[AEJKMO]{1}-[0-9]{3}$", message = "Registarska oznaka mora biti u 'A99-A-999' formatu")
	private String tablica;

	@Column(name = "reg_od")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date regOd;

	@Column(name = "reg_do")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date regDo;

	@Column(name = "osig_od")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date osigOd;

	@Column(name = "osig_do")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date osigDo;

	@Column(name = "aktivno")
	private Boolean jeAktivno;
	
	@ManyToOne( cascade = {CascadeType.DETACH, CascadeType.MERGE}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	private Vozilo vozilo;

	
	/**
	 * Implementacija compareTo metode kako bi mogli sortirati Registracije po tablicama!
	 */
	@Override
	public int compareTo(Registracija o) {
		return this.getTablica().compareTo(o.getTablica());
	}


	//***********************************************
	//*  			Getteri i Setteri 				*
	//*                    							*
	// **********************************************

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the regOd
	 */
	public Date getRegOd() {
		return regOd;
	}

	/**
	 * @param regOd the regOd to set
	 */
	public void setRegOd(Date regOd) {
		this.regOd = regOd;
	}

	/**
	 * @return the regDo
	 */
	public Date getRegDo() {
		return regDo;
	}

	/**
	 * @param regDo the regDo to set
	 */
	public void setRegDo(Date regDo) {
		this.regDo = regDo;
	}

	/**
	 * @return the osigOd
	 */
	public Date getOsigOd() {
		return osigOd;
	}

	/**
	 * @param osigOd the osigOd to set
	 */
	public void setOsigOd(Date osigOd) {
		this.osigOd = osigOd;
	}

	/**
	 * @return the osigDo
	 */
	public Date getOsigDo() {
		return osigDo;
	}

	/**
	 * @param osigDo the osigDo to set
	 */
	public void setOsigDo(Date osigDo) {
		this.osigDo = osigDo;
	}

	/**
	 * @return the jeAktivno
	 */
	public Boolean getJeAktivno() {
		return jeAktivno;
	}

	/**
	 * @param jeAktivno the jeAktivno to set
	 */
	public void setJeAktivno(Boolean jeAktivno) {
		this.jeAktivno = jeAktivno;
	}

	public String getTablica() {
		return tablica;
	}

	public void setTablica(String tablica) {
		this.tablica = tablica;
	}

	public Vozilo getVozilo() {
		return vozilo;
	}

	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}

}

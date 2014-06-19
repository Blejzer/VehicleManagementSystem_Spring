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
@Table(name="servis")
public class Servis implements Serializable{

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
	
	@ManyToOne( cascade = {CascadeType.REMOVE}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	@NotNull
	private Vozilo vozilo;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "servis_part", joinColumns = { 
			@JoinColumn(name = "servis_id", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "dio_id", 
					nullable = false, updatable = false) })
	private List<Dio> djelovi = new ArrayList<Dio>();
	
	@ManyToOne( cascade = {CascadeType.REMOVE}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false)
	@NotNull
	private VrstaServisa vrstaServisa;
	
	private Boolean zavrsen;

	
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
	 * @return the datum
	 */
	public Date getDatum() {
		return datum;
	}

	/**
	 * @param datum the datum to set
	 */
	public void setDatum(Date datum) {
		this.datum = datum;
	}

	/**
	 * @return the vozilo
	 */
	public Vozilo getVozilo() {
		return vozilo;
	}

	/**
	 * @param vozilo the vozilo to set
	 */
	public void setVozilo(Vozilo vozilo) {
		this.vozilo = vozilo;
	}

	/**
	 * @return the djelovi
	 */
	public List<Dio> getDjelovi() {
		return djelovi;
	}

	/**
	 * @param djelovi the djelovi to set
	 */
	public void setDjelovi(List<Dio> djelovi) {
		this.djelovi = djelovi;
	}

	/**
	 * @return the vrstaServisa
	 */
	public VrstaServisa getVrstaServisa() {
		return vrstaServisa;
	}

	/**
	 * @param vrstaServisa the vrstaServisa to set
	 */
	public void setVrstaServisa(VrstaServisa vrstaServisa) {
		this.vrstaServisa = vrstaServisa;
	}

	/**
	 * @return the zavrsen
	 */
	public Boolean getZavrsen() {
		return zavrsen;
	}

	/**
	 * @param zavrsen the zavrsen to set
	 */
	public void setZavrsen(Boolean zavrsen) {
		this.zavrsen = zavrsen;
	}

}

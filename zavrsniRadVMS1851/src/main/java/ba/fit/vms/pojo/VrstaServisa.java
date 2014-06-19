package ba.fit.vms.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="vrsta_servisa")
public class VrstaServisa implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="naziv")
	@NotNull(message= "Naziv vrste servisa ne moze biti prazan")
	private String naziv;

	
	
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
	 * @return the naziv
	 */
	public String getNaziv() {
		return naziv;
	}

	/**
	 * @param naziv the naziv to set
	 */
	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}
	
	

}

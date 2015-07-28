package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "vozilo")
@NamedQuery(name = Vozilo.TRAZI_PO_VIN, query = "select v from Vozilo v where v.vin = :vin")
public class Vozilo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String TRAZI_PO_VIN = "Vozilo.traziPoVin";
	
	@Id
	@Column(name = "vin", unique = true, nullable = false)
	@Size(min=17, max=17, message="VIN broj mora imati tacno 17 karaktera.")
	private String vin;
	
	@Column(name = "marka")
	@NotEmpty(message= "Marka vozila ne moze biti prazna")
	@Pattern(regexp="[a-z]*", flags = Pattern.Flag.CASE_INSENSITIVE)
	private String marka;
	
	@Column(name = "model")
	@NotEmpty
	private String model;
	
	@Column(name = "godina")
	@NotNull
	@Min(1995)
	@Max(2013)
	private Integer godina;
	
	@Column(name = "boja")
	@NotEmpty
	@Pattern(regexp="[a-z]*", flags = Pattern.Flag.CASE_INSENSITIVE)
	private String boja;

	
	/*@ManyToMany(fetch = FetchType.EAGER, mappedBy = "vozila")
	private Set<Korisnik> vozaci;*/
	
	
	//***********************************************
	//*  			Getteri i Setteri 				*
	//*                    							*
	// **********************************************
	/**
	 * @return the vin
	 */
	public String getVin() {
		return vin;
	}

	/**
	 * @param vin the vin to set
	 */
	public void setVin(String vin) {
		this.vin = vin;
	}

	/**
	 * @return the marka
	 */
	public String getMarka() {
		return marka;
	}

	/**
	 * @param marka the marka to set
	 */
	public void setMarka(String marka) {
		this.marka = marka;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * @return the godina
	 */
	public Integer getGodina() {
		return godina;
	}

	/**
	 * @param godina the godina to set
	 */
	public void setGodina(Integer godina) {
		this.godina = godina;
	}

	/**
	 * @return the boja
	 */
	public String getBoja() {
		return boja;
	}

	/**
	 * @param boja the boja to set
	 */
	public void setBoja(String boja) {
		this.boja = boja;
	}	
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
		return false;
		}
		if (getClass() != obj.getClass()) {
		return false;
		}
		final Vozilo  other = (Vozilo) obj;
		if (!Objects.equals(this.vin, other.vin)) {
		return false;
		}
		return true;
		}

}

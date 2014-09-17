package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Date;

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
	
	@ManyToOne( cascade = {CascadeType.PERSIST}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false, updatable=false)
	private Kilometraza kilometraza;
	
	@ManyToOne( cascade = {CascadeType.PERSIST}, fetch=FetchType.EAGER )
	@JoinColumn(nullable=false, updatable=false)
	private Lokacija lokacija;
	
	@Column(name = "datum")
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@NotNull
	private Date datum;

}

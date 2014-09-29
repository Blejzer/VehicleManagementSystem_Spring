package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
/**
 * 
 * @author nikola
 *
 */
@Entity
@Table(name="kilometraza")
public class Kilometraza implements Serializable {

		private static final long serialVersionUID = 1L;
		
		@Id
		@GeneratedValue
		@Column(name="id")
		private Long id;
		
		@Column(name="kilometri")
		@NotNull(message= "Trenutna kilometraza ne moze biti prazna")
		private Long kilometraza;
		
		@Column(name = "datum")
		@DateTimeFormat(pattern = "yyyy-MM-dd")
		@NotNull
		private Date datum;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getKilometraza() {
			return kilometraza;
		}

		public void setKilometraza(Long kilometraza) {
			this.kilometraza = kilometraza;
		}

		public Date getDatum() {
			return datum;
		}

		public void setDatum(Date datum) {
			this.datum = datum;
		}
}

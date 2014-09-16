package ba.fit.vms.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "korisnik")
@NamedQuery(name = Korisnik.READ_BY_EMAIL, query = "select k from Korisnik k where k.email = :email")
public class Korisnik implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String READ_BY_EMAIL = "Korisnik.findByEmail";

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(unique = true, nullable = false)
	@NotNull
	@NotEmpty
	private String email;

	@Column
	@NotNull
	@NotEmpty
	private String ime;

	@Column
	@NotNull
	@NotEmpty
	private String prezime;

	@Column
	private Boolean jeAktivan;

	@JsonIgnore
	private String lozinka;

	private String rola = "ROLE_USER";
	
	@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @JoinTable(name="korisnik_vozilo",
                joinColumns={@JoinColumn(name="korisnik_id")},
                inverseJoinColumns={@JoinColumn(name="vozilo_vin")})
	private Set<Vozilo> vozila = new HashSet<Vozilo>();
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "korisnik")
	private List<Tiket> tiketi = new ArrayList<Tiket>();

	
	//***********************************************
	//*  			Konstruktori 					*
	//*                    							*
	// **********************************************
	
	public Korisnik(String email, String ime, String prezime,
			Boolean jeAktivan, String lozinka, String rola) {
		this.email = email;
		this.ime = ime;
		this.prezime = prezime;
		this.jeAktivan = jeAktivan;
		this.lozinka = lozinka;
		this.rola = rola;
	}

	public Korisnik(){
		
	};
	

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

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the ime
	 */
	public String getIme() {
		return ime;
	}

	/**
	 * @param ime the ime to set
	 */
	public void setIme(String ime) {
		this.ime = ime;
	}

	/**
	 * @return the prezime
	 */
	public String getPrezime() {
		return prezime;
	}

	/**
	 * @param prezime the prezime to set
	 */
	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	/**
	 * @return the jeAktivan
	 */
	public Boolean getJeAktivan() {
		return jeAktivan;
	}

	/**
	 * @param jeAktivan the jeAktivan to set
	 */
	public void setJeAktivan(Boolean jeAktivan) {
		this.jeAktivan = jeAktivan;
	}

	/**
	 * @return the lozinka
	 */
	public String getLozinka() {
		return lozinka;
	}

	/**
	 * @param lozinka the lozinka to set
	 */
	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	/**
	 * @return the rola
	 */
	public String getRola() {
		return rola;
	}

	/**
	 * @param rola the rola to set
	 */
	public void setRola(String rola) {
		this.rola = rola;
	}

	/**
	 * @return lista vozila
	 */
	public Set<Vozilo> getVozila() {
		return this.vozila;
	}

	/**
	 * @param vozila
	 */
	public void setVozila(Set<Vozilo> vozila) {
		this.vozila = vozila;
	}

	/**
	 * @return lista tiketa
	 */
	public List<Tiket> getTiketi() {
		return this.tiketi;
	}

	public void setTiketi(List<Tiket> tiketi) {
		this.tiketi = tiketi;
	}

	//***********************************************
	//*  			Override equals 				*
	//*                    							*
	// **********************************************

	/**
	 * Na ovaj nacin radimo override klasicnog poredjenja 
	 * te kreiramo sopstveno poredjenje koje nam treba
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
		return false;
		}
		if (getClass() != obj.getClass()) {
		return false;
		}
		final Korisnik other = (Korisnik) obj;
		if (!Objects.equals(this.id, other.id)) {
		return false;
		}
		return true;
		}
}

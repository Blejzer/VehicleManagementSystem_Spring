package ba.fit.vms.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Registracija;

@Repository
@Transactional(readOnly = true)
public class RegistracijaRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	//***********************************************
	//*					REST METODE					*
	//*												*
	//***********************************************
	
	/**
	 * Metoda pronalazi registraciju po ID
	 * @param id
	 * @return
	 */
	@Transactional
	public Registracija read(Long id){
		
		try {
			return entityManager.find(Registracija.class, id);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Metoda dodaje novu registraciju
	 * @param registracija
	 * @return
	 */
	@Transactional(readOnly = false)
	public Registracija save(Registracija registracija){
		String prva = "select r from Registracija r where r.vozilo.vin = :vin";
		List<Registracija> testNull = new ArrayList<Registracija>();
		Registracija zaSnimitiNova = new Registracija();
		Registracija testReg = new Registracija();
		try {
			testNull = entityManager.createQuery(prva).setParameter("vin", registracija.getVozilo().getVin()).getResultList();
		} catch (Exception e) {
			return null;
		}
		if(testNull.size() !=0){
			for (Registracija reg : testNull) {
				if(reg.getJeAktivno()){
					testReg = reg;
				}
			}
		}
	}
	
	/**
	 * Metoda azurira postojecu registraciju
	 * @param registracija
	 * @return
	 */
	@Transactional
	public Registracija update(Registracija registracija){
		try {
			entityManager.merge(registracija);
			return registracija;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Metoda brise postojecu registraciju
	 * @param id
	 * @return
	 */
	@Transactional
	public Registracija delete(Long id){
		try {
			Registracija zaBrisati = entityManager.find(Registracija.class, id);
			return zaBrisati;
		} catch (Exception e) {
			return null;
		}
	}
	
	//***********************************************
	//*					LIST METODE					*
	//*												*
	//***********************************************
	
	public List<Registracija> getSveRegistracijaZaVIN(String vin){
		
	}

}

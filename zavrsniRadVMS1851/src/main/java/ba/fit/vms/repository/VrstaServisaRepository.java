package ba.fit.vms.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.VrstaServisa;

@Repository
@Transactional(readOnly = true)
public class VrstaServisaRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	//***********************************************
	//*					REST METODE					*
	//*												*
	//***********************************************
	
	/**
	 * Metoda vraca pronadjenu Vrstu Servisa
	 * @param id
	 * @return
	 */
	@Transactional
	public VrstaServisa read(Long id){
		try {
			VrstaServisa nadjen = entityManager.find(VrstaServisa.class, id);
			return nadjen;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/**
	 * Metoda snima novu Vrstu Servisa
	 * @param vServis
	 * @return
	 */
	@Transactional(readOnly = false)
	public VrstaServisa save(VrstaServisa vServis){
		try {
			entityManager.persist(vServis);
			return vServis;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/**
	 * Metoda azurira postojecu Vrstu Servisa
	 * @param vServis
	 * @return
	 */
	@Transactional
	public VrstaServisa update(VrstaServisa vServis){
		try {
			entityManager.merge(vServis);
			return vServis;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Metoda brise Vrstu Servisa
	 * @param id
	 * @return
	 */
	@Transactional
	public VrstaServisa delete(Long id){
		try {
			VrstaServisa zaBrisati = entityManager.find(VrstaServisa.class, id);
			entityManager.remove(zaBrisati);
			return zaBrisati;
		} catch (Exception e) {
			return null;
		}
	}
	
	//***********************************************
	//*					LIST METODE					*
	//*												*
	//***********************************************
	
	/**
	 * Metoda vraca sve vrste servisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VrstaServisa> getSveVrsteServisa(){
		try {
			List<VrstaServisa> sveVrsteServisa = entityManager.createQuery("from VrstaServisa").getResultList();
			return sveVrsteServisa;
		} catch (Exception e) {
			return new ArrayList<VrstaServisa>();
		}
	}

}

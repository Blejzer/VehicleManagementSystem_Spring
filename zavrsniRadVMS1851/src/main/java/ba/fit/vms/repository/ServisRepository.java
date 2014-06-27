package ba.fit.vms.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Servis;

@Repository
@Transactional(readOnly = true)
public class ServisRepository {
	
	// Dodajemo Entity Managera
	@PersistenceContext
	private EntityManager entityManager;
	
	//***********************************************
	//*					REST METODE					*
	//*												*
	//***********************************************
	
	public Servis read(Long id){
		try {
			return entityManager.find(Servis.class, id);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public Servis edit(Servis servis){
		try {
			entityManager.merge(servis);
			return servis;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public Servis save(Servis servis){
		try {
			entityManager.merge(servis);
			return servis;
		} catch (PersistenceException e) {
			return null;
		}
	}
		
	public Servis delete(Long id){
		try {
			Servis zaBrisati = this.read(id);
			entityManager.remove(zaBrisati);
			entityManager.flush();
			return zaBrisati;
		} catch (Exception e) {
			return null;
		}
	}

	//***********************************************
	//*					LIST METODE					*
	//*												*
	//***********************************************
	
	
	
}

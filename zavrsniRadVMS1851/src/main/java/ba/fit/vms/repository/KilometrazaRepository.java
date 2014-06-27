package ba.fit.vms.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Kilometraza;

@Repository
@Transactional(readOnly = true)
public class KilometrazaRepository {
	
	// Dodajemo Entity Managera
	@PersistenceContext
	private EntityManager entityManager;
	
	//***********************************************
	//*					REST METODE					*
	//*												*
	//***********************************************
	
	public Kilometraza read(Long id){
		try {
			return entityManager.find(Kilometraza.class, id);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public Kilometraza edit(Kilometraza kilo){
		try {
			entityManager.merge(kilo);
			return kilo;
		} catch (Exception e) {
			return null;
		}
	}
	
	@Transactional
	public Kilometraza save(Kilometraza kilo){
		try {
			entityManager.persist(kilo);
			return kilo;
		} catch (Exception e) {
			return null;
		}
	}
	
	public Kilometraza delete(Long id){
		try {
			Kilometraza zaBrisati = this.read(id);
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

package ba.fit.vms.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Dio;

@Repository
@Transactional(readOnly = true)
public class DioRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	//***********************************************
	//*					REST METODE					*
	//*												*
	//***********************************************
	
	/**
	 * trazenje dijela
	 * @param id
	 * @return
	 */
	@Transactional
	public Dio read(Long id){
		try {
			Dio nadjen = entityManager.find(Dio.class, id);
			return nadjen;
		} catch (PersistenceException e) {
			return null;
		}
	}
	/**
	 * Snimanje novog dijela
	 * @param dio
	 * @return
	 */
	@Transactional(readOnly = false)
	public Dio save(Dio dio){
		try {
			entityManager.persist(dio);
			return dio;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/**
	 * Izmjena podataka za dio
	 * @param dio
	 * @return
	 */
	@Transactional
	public Dio update(Dio dio){
		try {
			entityManager.merge(dio);
			return dio;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/**
	 * Brisanje dijela
	 * @param id
	 * @return
	 */
	@Transactional
	public Dio delete(Long id){
		Dio zaBrisati = entityManager.find(Dio.class, id);
		try {
			entityManager.remove(zaBrisati);
			entityManager.flush();
			return zaBrisati;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	//***********************************************
	//*					LIST METODE					*
	//*												*
	//***********************************************
	
	/**
	 * Metoda vraca sve dijelove iz tabele Dio
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Dio> getSviDijelovi(){
		try {
			return entityManager.createQuery("from Dio").getResultList();
		} catch (Exception e) {
			return new ArrayList<Dio>();
		}
	}
}

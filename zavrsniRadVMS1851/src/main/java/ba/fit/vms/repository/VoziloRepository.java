package ba.fit.vms.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Vozilo;

@Repository
@Transactional(readOnly = true)
public class VoziloRepository {
	
	// Dodajemo Entity Managera
		@PersistenceContext
		private EntityManager entityManager;

		public Vozilo findByVin(String vin) {
			try {
				return entityManager.find(Vozilo.class, vin);
			} catch (PersistenceException e) {
				return null;
			}
		}
		
		

}

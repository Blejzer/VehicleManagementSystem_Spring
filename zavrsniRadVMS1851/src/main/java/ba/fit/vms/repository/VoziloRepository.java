package ba.fit.vms.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Vozilo;

@Repository
@Transactional(readOnly = true)
public class VoziloRepository {
	
	// Dodajemo Entity Managera
		@PersistenceContext
		private EntityManager entityManager;

		//***********************************************
		//*					REST METODE					*
		//*												*
		//***********************************************		
		
		/**
		 * Metoda vraca pronadjeno vozilo 
		 * @param vin
		 * @return
		 */
		public Vozilo read(String vin) {
			try {
				return entityManager.find(Vozilo.class, vin);
			} catch (PersistenceException e) {
				return null;
			}
		}
		
		/**
		 * Metoda azurira odabrano vozilo
		 * @param vozilo
		 * @return
		 */
		@Transactional
		public Vozilo edit(Vozilo vozilo){
			try {
				entityManager.merge(vozilo);
				return vozilo;
			} catch (PersistenceException e) {
				return null;
			}
		}
		
		/**
		 * Metoda snima novo vozilo
		 * @param vozilo
		 * @return
		 */
		@Transactional
		public Vozilo save(Vozilo vozilo){
			try {
				entityManager.persist(vozilo);
				return vozilo;
			} catch (PersistenceException e) {
				return null;
			}
		}
		
		/**
		 * Metoda brise odabrano vozilo
		 * @param vin
		 * @return
		 */
		public Vozilo delete(String vin){
			try {
				Vozilo zaBrisati = this.read(vin);
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
		 * Metoda vraca listu svih vozila
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@Transactional
		public List<Vozilo> getSvaVozila(){
			try {
				Query upit = entityManager.createQuery("from Vozilo v");
				return upit.getResultList();
			} catch (PersistenceException e) {
				return new ArrayList<Vozilo>();
			}
		}
		
		/**
		 * Metoda vraca listu registrovanih vozila
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public List<Vozilo> getSvaRegVozila(){
			try {
				Query upit = entityManager.createQuery("select v from Vozilo v where "
						+ "v.vin in (select vozilo.vin from Registracija)");
				return upit.getResultList();
			} catch (PersistenceException e) {
				return new ArrayList<Vozilo>();
			}
		}
		
		/**
		 * Metoda vraca listu neregistrovanih vozila
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public List<Vozilo> getSvaNeRegVozila(){
			try {
				Query upit = entityManager.createQuery("select v from Vozilo v where "
						+ "v.vin not in (select vozilo.vin from Registracija)");
				return upit.getResultList();
			} catch (PersistenceException e) {
				return new ArrayList<Vozilo>();
			}
		}

}

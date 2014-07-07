package ba.fit.vms.repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

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
	
	/**
	 * Metoda vraca pronadjeni Servis
	 * @param id
	 * @return
	 */
	public Servis read(Long id){
		try {
			return entityManager.find(Servis.class, id);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Metoda azurira izmjenjeni Servis
	 * @param servis
	 * @return
	 */
	@Transactional
	public Servis edit(Servis servis){
		try {
			entityManager.merge(servis);
			return servis;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Metoda snima novi Servis
	 * @param servis
	 * @return
	 */
	@Transactional
	public Servis save(Servis servis){
		try {
			entityManager.merge(servis);
			return servis;
		} catch (PersistenceException e) {
			return null;
		}
	}
		
	/**
	 * Metoda brise odabrani servis
	 * @param id
	 * @return
	 */
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
	
	
	//===============================================
	//=					GLOBALNE					=
	//===============================================
	/**
	 * Metoda vraca listu svih servisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviServisi(){
		try {
			Query upit = entityManager.createQuery("from Servis");
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca listu svih odradjenih servisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviOdradjeniServisi(){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "s.completed = true");
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca listu svih zakazanih servisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviZakazaniServisi(){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "s.completed = false");
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca sve servise u datoj godini
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviServisiPoGodini(Date date){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "YEAR(s.datum) = YEAR(:date)")
					.setParameter("date", date);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca sve servise u datom mjesecu
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviServisiPoMjesecu(Date date){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "YEAR(s.datum) = YEAR(:date) and MONTH(s.datum) = MONTH(:date)")
					.setParameter("date", date);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	
	
	//===============================================
	//=					INDIVIDUALNE				=
	//===============================================
	
	/**
	 * Metoda vraca listu svih servisa za odabrano vozilo (VIN)
	 * @param vin
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviServisiPoVIN(String vin){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "s.vozilo.vin = :vin order by s.datum desc").setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca listu svih zakazanih servisa za dato vozilo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviZakazaniServisiZaVozilo(String vin){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "s.completed = false "
					+ "and s.vozilo.vin = :vin").setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca listu svih odradjenih servisa za dato vozilo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviOdradjeniServisiZaVozilo(String vin){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "s.completed = true "
					+ "and s.vozilo.vin = :vin").setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca sve servise u datoj godini za odabrano vozilo
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviServisiPoGodiniZaVozilo(Date date, String vin){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "YEAR(s.datum) = YEAR(:date) and "
					+ "s.vozilo.vin = :vin")
					.setParameter("date", date)
					.setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
	/**
	 * Metoda vraca sve servise u datom mjesecu za odabrano vozilo
	 * @param date
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Servis> getSviServisiPoMjesecuZaVozilo(Date date, String vin){
		try {
			Query upit = entityManager.createQuery("select s from Servis s where "
					+ "YEAR(s.datum) = YEAR(:date) and MONTH(s.datum) = MONTH(:date) and "
					+ "s.vozilo.vin = :vin")
					.setParameter("date", date)
					.setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<Servis>();
		}
	}
	
}

package ba.fit.vms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.StariServis;

@Repository
@Transactional(readOnly = true)
public interface StariServisRepository  extends JpaRepository<StariServis, Long> {
	
	List<StariServis> findByZavrsenTrue(Pageable pageable);
	Page<StariServis> findByZavrsenFalse(Pageable pageable);
	
	Page<StariServis> findAllByVozilo_Vin(String vin, Pageable pageable);
	
	@Query("select s from StariServis s where s.vozilo.vin=:vin and YEAR(s.datum)=:year and MONTH(s.datum)=:month order by s.datum DESC")
	List<StariServis> getCustomServis(@Param("vin") String vin, @Param("year") int year, @Param("month") int month);
	

}
/*	
	//***********************************************
	//*					LIST METODE					*
	//*												*
	//***********************************************
	
	
	//===============================================
	//=					GLOBALNE					=
	//===============================================

	
	*//**
	 * Metoda vraca listu svih odradjenih servisa
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviOdradjeniServisi(){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "s.completed = true");
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	*//**
	 * Metoda vraca listu svih zakazanih servisa
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviZakazaniServisi(){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "s.completed = false");
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	*//**
	 * Metoda vraca sve servise u datoj godini
	 * @param date
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviServisiPoGodini(Date date){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "YEAR(s.datum) = YEAR(:date)")
					.setParameter("date", date);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	*//**
	 * Metoda vraca sve servise u datom mjesecu
	 * @param date
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviServisiPoMjesecu(Date date){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "YEAR(s.datum) = YEAR(:date) and MONTH(s.datum) = MONTH(:date)")
					.setParameter("date", date);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	
	
	//===============================================
	//=					INDIVIDUALNE				=
	//===============================================
	
	*//**
	 * Metoda vraca listu svih servisa za odabrano vozilo (VIN)
	 * @param vin
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviServisiPoVIN(String vin){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "s.vozilo.vin = :vin order by s.datum desc").setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	*//**
	 * Metoda vraca listu svih zakazanih servisa za dato vozilo
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviZakazaniServisiZaVozilo(String vin){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "s.completed = false "
					+ "and s.vozilo.vin = :vin").setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	*//**
	 * Metoda vraca listu svih odradjenih servisa za dato vozilo
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviOdradjeniServisiZaVozilo(String vin){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "s.completed = true "
					+ "and s.vozilo.vin = :vin").setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	*//**
	 * Metoda vraca sve servise u datoj godini za odabrano vozilo
	 * @param date
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviServisiPoGodiniZaVozilo(Date date, String vin){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "YEAR(s.datum) = YEAR(:date) and "
					+ "s.vozilo.vin = :vin")
					.setParameter("date", date)
					.setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
	*//**
	 * Metoda vraca sve servise u datom mjesecu za odabrano vozilo
	 * @param date
	 * @return
	 *//*
	@SuppressWarnings("unchecked")
	public List<StariServis> getSviServisiPoMjesecuZaVozilo(Date date, String vin){
		try {
			Query upit = entityManager.createQuery("select s from StariServis s where "
					+ "YEAR(s.datum) = YEAR(:date) and MONTH(s.datum) = MONTH(:date) and "
					+ "s.vozilo.vin = :vin")
					.setParameter("date", date)
					.setParameter("vin", vin);
			return upit.getResultList();
		} catch (PersistenceException e) {
			return new ArrayList<StariServis>();
		}
	}
	
}
*/
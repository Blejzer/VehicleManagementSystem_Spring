package ba.fit.vms.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.LokacijaKilometraza;

@Repository
@Transactional
public interface LokacijaKilometrazaRepository extends
		JpaRepository<LokacijaKilometraza, Long> {

	List<LokacijaKilometraza> findByKorisnikVozilo_Id(Long id);

	@Query("select m from LokacijaKilometraza m where m.korisnikVozilo.vozilo.vin = ?1 and YEAR(m.datum) = ?2 and MONTH(m.datum) = ?3 order by m.kilometraza DESC")
	List<LokacijaKilometraza> pregled(String vin, Integer godina, Integer mjesec);
	
	// Primjer koristenja NATIVE QUERY metode - trazimo maksimalnu kilometrazu!
	@Query(value = "SELECT t1.* FROM lokacija_kilometraza t1 INNER JOIN (SELECT * FROM lokacija_kilometraza WHERE `korisnikVozilo_id` IN (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) t2 ON t1.id = t2.id WHERE t1.kilometraza = (SELECT max(kilometraza) from lokacija_kilometraza WHERE `korisnikVozilo_id` in (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) group by t1.id;", nativeQuery = true)
	LokacijaKilometraza getMaxMileage(String vin);
	
	@Query(value="SELECT t1.* FROM lokacija_kilometraza t1, (SELECT * FROM lokacija_kilometraza WHERE `korisnikVozilo_id` IN (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) t2 WHERE t1.kilometraza = (SELECT max(kilometraza) from lokacija_kilometraza WHERE `korisnikVozilo_id` in (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1) and MONTH(datum)=?2 and YEAR(datum)=?3) group by t1.id", nativeQuery=true)
	LokacijaKilometraza getMaxMileagePrevious(String vin, Integer mjesec, Integer godina);
	
	@Query(value="SELECT t1.* FROM lokacija_kilometraza t1, (SELECT * FROM lokacija_kilometraza WHERE `korisnikVozilo_id` IN (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) t2 WHERE MONTH(t1.`datum`)=?2 and YEAR(t1.`datum`)=?3 and t1.kilometraza = (SELECT min(kilometraza) from lokacija_kilometraza WHERE MONTH(datum)=?2 and YEAR(datum)=?3 and `korisnikVozilo_id` in (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) group by t1.id", nativeQuery=true)
	LokacijaKilometraza getMinMileagePrevious(String vin, Integer mjesec, Integer godina);
	
	Page<LokacijaKilometraza> findByKorisnikVozilo_VoziloVinOrderByKilometrazaDesc(String vin, Pageable pageable);
	
	LokacijaKilometraza findMaxKilometrazaByKorisnikVozilo_VoziloVin(String vin);
	
	@Query(value="SELECT t1.* FROM lokacija_kilometraza t1, (SELECT * FROM lokacija_kilometraza WHERE `korisnikVozilo_id` IN (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) t2 WHERE t1.kilometraza = (SELECT max(kilometraza) from lokacija_kilometraza WHERE `korisnikVozilo_id` in (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1) and datum<?2) group by t1.id", nativeQuery=true)
	LokacijaKilometraza getMaxMileagePrevious2(String vin, Date d);
	
	// Odaberi maksimalnu kilometrazu izmedju datuma
	@Query(value="SELECT t1.* FROM lokacija_kilometraza t1 INNER JOIN (SELECT * FROM lokacija_kilometraza WHERE `korisnikVozilo_id` IN (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) t2 ON t1.id = t2.id WHERE t1.kilometraza = (SELECT max(kilometraza) from lokacija_kilometraza WHERE `korisnikVozilo_id` in (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1 AND datum BETWEEN ?2 AND ?3 )) group by t1.id", nativeQuery=true)
	LokacijaKilometraza getMaxKilo1(String vin, Date d1, Date d2);

	// Odaberi maksimalnu kilometrazu prije datuma
	@Query(value="SELECT t1.* FROM lokacija_kilometraza t1 INNER JOIN (SELECT * FROM lokacija_kilometraza WHERE `korisnikVozilo_id` IN (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) t2 ON t1.id = t2.id WHERE t1.kilometraza = (SELECT max(kilometraza) from lokacija_kilometraza WHERE `korisnikVozilo_id` in (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1 AND datum < ?2 )) group by t1.id", nativeQuery=true)
	LokacijaKilometraza getMaxKilo2(String vin, Date d);
	
	// Odaberi minimalnu kilometrazu nakon datuma
	@Query(value="SELECT t1.* FROM lokacija_kilometraza t1 INNER JOIN (SELECT * FROM lokacija_kilometraza WHERE `korisnikVozilo_id` IN (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1)) t2 ON t1.id = t2.id WHERE t1.kilometraza = (SELECT min(kilometraza) from lokacija_kilometraza WHERE `korisnikVozilo_id` in (SELECT id FROM korisnik_vozilo WHERE `vozilo_vin`=?1 AND datum > ?2 )) group by t1.id", nativeQuery=true)
	LokacijaKilometraza getMaxKilo3(String vin, Date d);

}

package ba.fit.vms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.KorisnikVozilo;
import ba.fit.vms.pojo.Vozilo;

@Repository
@Transactional
public interface KorisnikVoziloRepository extends JpaRepository<KorisnikVozilo, Long> {
	
	@Query("select distinct k.korisnik from KorisnikVozilo k")
	List<Korisnik> findKorisnik();
	
	@Query("select c from Korisnik c where c.jeAktivan is true and c.id not in (select v.korisnik.id from KorisnikVozilo v where v.vraceno is null)")
	List<Korisnik> findAllUnassigned();
	
	@Query("select c from Vozilo c where c.vin not in (SELECT c FROM Vozilo c WHERE c.vin in (SELECT v.vozilo.vin FROM KorisnikVozilo v WHERE v.vraceno IS null AND v.vozilo.vin IN (SELECT DISTINCT c.vozilo.vin FROM KorisnikVozilo c)))")
	List<Vozilo> findAllUnassignedV();
	
	@Query("SELECT c FROM Vozilo c WHERE c.vin in (SELECT v.vozilo.vin FROM KorisnikVozilo v WHERE v.vraceno IS null AND v.vozilo.vin IN (SELECT DISTINCT c.vozilo.vin FROM KorisnikVozilo c))")
	List<Vozilo> findAllAssignedV();
	
	List<KorisnikVozilo> findAllByVozilo_VinOrderByVracenoDesc(String vin);
	
	List<KorisnikVozilo> findAllByKorisnik_Id(Long id);
	
	KorisnikVozilo findByKorisnik_EmailAndVracenoNull(String email);
	KorisnikVozilo findByKorisnik_IdAndVracenoNull(Long id);
	
	KorisnikVozilo findByVozilo_VinAndVracenoNull(String vin);
	
	Page<KorisnikVozilo> findAllByVozilo_VinAndVracenoIsNotNullOrderByVracenoAsc(String vin, Pageable pageable);
	
	@Query("select a from KorisnikVozilo a where a.vozilo.vin=?1 and a.vraceno in(select max(b.vraceno) from KorisnikVozilo b where b.vozilo.vin =?1)")
	KorisnikVozilo findLast(String vin);	
	
	List<KorisnikVozilo> findByVracenoNull();
	
}

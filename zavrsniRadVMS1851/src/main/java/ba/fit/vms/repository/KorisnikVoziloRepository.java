package ba.fit.vms.repository;

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
	
	@Query("select c from Korisnik c where c.id not in (select v.korisnik.id from KorisnikVozilo v where v.returnedDate is null)")
	List<Korisnik> findAllUnassigned();
	
	@Query("select c from Account c where c.vin not in (select c.vehicle.vin from ConsultantVehicle c)")
	List<Vozilo> findAllUnassignedV();
	
	

}

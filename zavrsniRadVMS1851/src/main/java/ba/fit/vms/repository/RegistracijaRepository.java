package ba.fit.vms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Registracija;

@Repository
@Transactional(readOnly = true)
public interface RegistracijaRepository  extends JpaRepository<Registracija, Long> {
	
	@Query("select r from Registracija r where r.vozilo.vin =?1")
	List<Registracija> findRegsForVozilo(String vin);
	
	@Query("select r from Registracija r where r.vozilo.vin =?1 and r.jeAktivno=true")
	Registracija findActiveRegForVozilo(String vin);

}
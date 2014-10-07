package ba.fit.vms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.LokacijaKilometraza;

@Repository
@Transactional
public interface LokacijaKilometrazaRepository extends
		JpaRepository<LokacijaKilometraza, Long> {

	List<LokacijaKilometraza> findByKorisnikVozilo_Id(Long id);

}

package ba.fit.vms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ba.fit.vms.pojo.Tiket;

@Repository
public interface TiketRepository extends JpaRepository<Tiket, Long> {
	
	List<Tiket> findAllByKorisnik_Id(Long id);
	Page<Tiket> findAllByKorisnik_Id(Long id, Pageable pageable);
	Page<Tiket> findAllByKorisnik_IdAndRijesenDatumNullOrderByTiketDatumDesc(Long id, Pageable pageable);
	Page<Tiket> findAllByKorisnik_IdAndRijesenDatumNotNullOrderByTiketDatumDesc(Long id, Pageable pageable);
	Page<Tiket> findAllByRijesenDatumNullOrderByTiketDatumDesc(Pageable pageable);
	List<Tiket> findAllByKorisnik_IdAndVozilo_vinAndPrethodniNotNullOrderByIdDesc(Long id, String vin);
	List<Tiket> findAllByKorisnik_IdAndVozilo_vinAndPrethodniNullOrderByIdDesc(Long id, String vin);
	List<Tiket> findAllByKorisnik_IdAndVozilo_vinOrderByIdDesc(Long id, String vin);
	List<Tiket> findAllByPrethodniNotNullOrderByIdDesc();
	List<Tiket> findAllByPrethodniNullOrderByIdDesc();

	Page<Tiket> findAllByRijesenDatumNotNullOrderByTiketDatumDesc(Pageable pageable);
	Page<Tiket> findAll(Pageable pageable);
	
	Page<Tiket> findAllByIdIn(List<Long> ids, Pageable pageable);
	

	
}

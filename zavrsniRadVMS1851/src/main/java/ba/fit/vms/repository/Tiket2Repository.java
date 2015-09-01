package ba.fit.vms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ba.fit.vms.pojo.Korisnik;
import ba.fit.vms.pojo.Poruka;
import ba.fit.vms.pojo.Tiket2;

public interface Tiket2Repository extends JpaRepository<Tiket2, Long> {
	
	public Page<Tiket2> findByRijesenDatumIsNullAndKorisnikOrderByTiketDatumDesc(Korisnik korisnik, Pageable pageable);
	public Page<Tiket2> findByRijesenDatumIsNullOrderByTiketDatumDesc(Pageable pageable);
	public Page<Tiket2> findByRijesenDatumIsNotNullAndKorisnikOrderByTiketDatumDesc(Korisnik korisnik, Pageable pageable);
	public Page<Tiket2> findByRijesenDatumIsNotNullOrderByTiketDatumDesc(Pageable pageable);
	
	public Page<Poruka> findPorukeById(Long tid, Pageable pageable);

}

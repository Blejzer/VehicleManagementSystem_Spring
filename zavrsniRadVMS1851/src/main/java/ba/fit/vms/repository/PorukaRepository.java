package ba.fit.vms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ba.fit.vms.pojo.Poruka;

public interface PorukaRepository extends JpaRepository<Poruka, Long> {
	
	@Query("select p from Poruka where p in (select t.poruke from Tiket2 t where t.id=?1)")
	public Page<Poruka> findAllPoruke(Long tid, Pageable pageable);
	
}

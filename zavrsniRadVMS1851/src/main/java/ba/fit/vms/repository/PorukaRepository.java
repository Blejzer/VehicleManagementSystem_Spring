package ba.fit.vms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ba.fit.vms.pojo.Poruka;

public interface PorukaRepository extends JpaRepository<Poruka, Long> {
	
}

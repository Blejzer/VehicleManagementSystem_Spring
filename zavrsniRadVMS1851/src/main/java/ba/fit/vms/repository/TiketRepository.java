package ba.fit.vms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ba.fit.vms.pojo.Tiket;

@Repository
public interface TiketRepository extends JpaRepository<Tiket, Long> {

	
}

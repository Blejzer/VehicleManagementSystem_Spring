package ba.fit.vms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ba.fit.vms.pojo.Servis1;

public interface Servis1Repository extends JpaRepository<Servis1, Long> {
	
	List<Servis1> findByZavrsenTrue(Pageable pageable);
	Page<Servis1> findByZavrsenFalse(Pageable pageable);
	
	Page<Servis1> findAllByVozilo_Vin(String vin, Pageable pageable);
	
	@Query("select s from Servis1 s where s.vozilo.vin=:vin and YEAR(s.datum)=:year and MONTH(s.datum)=:month order by s.datum DESC")
	List<Servis1> getCustomServis(@Param("vin") String vin, @Param("year") int year, @Param("month") int month);

}

package ba.fit.vms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Korisnik;

@Repository
@Transactional
public interface KorisnikRepository extends CrudRepository<Korisnik, Long> {
	
	@Query("select k from Korisnik k where k.email = :email")
    public Korisnik find(@Param("email") String email);
	

}

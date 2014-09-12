package ba.fit.vms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Registracija;

@Repository
@Transactional(readOnly = true)
public interface RegistracijaRepository  extends JpaRepository<Registracija, Long> {

}
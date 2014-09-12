package ba.fit.vms.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import ba.fit.vms.pojo.VrstaServisa;

@Repository
public interface VrstaServisaRepository extends PagingAndSortingRepository<VrstaServisa, Long> {

}

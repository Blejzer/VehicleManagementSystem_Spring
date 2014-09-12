package ba.fit.vms.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Dio;

@Repository
@Transactional(readOnly = true)
public interface DioRepository extends PagingAndSortingRepository<Dio, Long>{
	
}

/**
 * 
 */
package ba.fit.vms.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ba.fit.vms.dao.PorukaRepositoryCustom;
import ba.fit.vms.pojo.Poruka;

/**
 * @author nikola
 *
 */
public class PorukaRepositoryCustomImpl implements PorukaRepositoryCustom {

	/*@PersistenceContext
	private EntityManager entityManager;
	 (non-Javadoc)
	 * @see ba.fit.vms.dao.PorukaRepositoryCustom#nadjiPoruke(ba.fit.vms.pojo.Tiket2, org.springframework.data.domain.Pageable)
	 
	@Override
	public List<Poruka> customFindPoruke(Long tid) {
		return null; //this.entityManager.createNativeQuery("SELECT DISTINCT * FROM poruka p where p.id IN (SELECT tp.poruke_id FROM tiket2_poruka tp WHERE tp.tiket2_id=?0 ORDER BY tp.`collection_id` DESC))").setParameter("0", tid).getResultList();
	}*/

}

package ba.fit.vms.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ba.fit.vms.dao.TiketDao;
import ba.fit.vms.pojo.Tiket;

@Repository
public class TiketDaoImpl implements TiketDao {
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Tiket create(Tiket tiket) {
		try {
			entityManager.persist(tiket);
			return tiket;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Tiket update(Tiket tiket) {
		try {
			entityManager.merge(tiket);
			return tiket;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Tiket delete(Tiket tiket) {
		try {
			entityManager.remove(tiket);
			entityManager.flush();
			return tiket;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Tiket read(Long id) {
		try {
			return entityManager.find(Tiket.class, id);
		} catch (Exception e) {
			return null;
		}
	}

}

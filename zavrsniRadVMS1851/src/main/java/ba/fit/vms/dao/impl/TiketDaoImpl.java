package ba.fit.vms.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ba.fit.vms.dao.TiketDao;
import ba.fit.vms.pojo.Tiket;

public class TiketDaoImpl implements TiketDao {
	
	// Dodajemo Entity Managera
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Tiket create(Tiket tiket) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Tiket tiket) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Tiket tiket) {
		// TODO Auto-generated method stub

	}

	@Override
	public Tiket read() {
		// TODO Auto-generated method stub
		return null;
	}

}

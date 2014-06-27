package ba.fit.vms.dao;

import ba.fit.vms.pojo.Tiket;

public interface TiketDao {


	public Tiket create(Tiket tiket);
	public void update(Tiket tiket);
	public void delete(Tiket tiket);
	public Tiket read();

}

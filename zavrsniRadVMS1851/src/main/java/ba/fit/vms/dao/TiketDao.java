package ba.fit.vms.dao;

import ba.fit.vms.pojo.Tiket;

public interface TiketDao {


	public Tiket create(Tiket tiket);
	public Tiket update(Tiket tiket);
	public Tiket delete(Tiket tiket);
	public Tiket read(Long id);

}

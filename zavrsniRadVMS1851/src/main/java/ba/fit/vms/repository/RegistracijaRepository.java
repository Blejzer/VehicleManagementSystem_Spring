package ba.fit.vms.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ba.fit.vms.pojo.Registracija;

@Repository
@Transactional(readOnly = true)
public class RegistracijaRepository {
	
	// Dodajemo Entity Managera
	@PersistenceContext
	private EntityManager entityManager;
	
	// Autowired repozitoriji
	@Autowired
	private VoziloRepository voziloRepository;
	
	//***********************************************
	//*					REST METODE					*
	//*												*
	//***********************************************
	
	/**
	 * Metoda pronalazi registraciju po ID
	 * @param id
	 * @return
	 */
	@Transactional
	public Registracija read(Long id){
		
		try {
			return entityManager.find(Registracija.class, id);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Metoda dodaje novu registraciju
	 * @param registracija
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public Registracija save(Registracija registracija){
		String prva = "select r from Registracija r where r.vozilo.vin = :vin";
		List<Registracija> testNull = new ArrayList<Registracija>();
		Registracija zaSnimitiNova = new Registracija();
		Registracija testReg = new Registracija();
		try {
			testNull = entityManager.createQuery(prva).setParameter("vin", registracija.getVozilo().getVin()).getResultList();
		} catch (Exception e) {
			return null;
		}
		if(testNull.size() !=0){
			for (Registracija reg : testNull) {
				if(reg.getJeAktivno()){
					testReg = reg;
				}
			}
			if(testReg.getJeAktivno()!=null){
				if(testReg.getRegOd().before(registracija.getRegOd())){
					zaSnimitiNova.setVozilo(voziloRepository.findByVin(registracija.getVozilo().getVin()));
					zaSnimitiNova.setOsigOd(registracija.getOsigOd());
					zaSnimitiNova.setOsigDo(registracija.getOsigDo());
					zaSnimitiNova.setRegDo(registracija.getRegDo());
					zaSnimitiNova.setRegOd(registracija.getRegOd());
					
					testReg.setJeAktivno(false);

					entityManager.merge(testReg);
					entityManager.persist(zaSnimitiNova);
					entityManager.flush();
					entityManager.close();
					return registracija;
				}else{
					if(testReg.getRegOd().after(registracija.getRegOd()) && !registracija.getJeAktivno()){
						zaSnimitiNova.setVozilo(voziloRepository.findByVin(registracija.getVozilo().getVin()));
						zaSnimitiNova.setOsigOd(registracija.getOsigOd());
						zaSnimitiNova.setOsigDo(registracija.getOsigDo());
						zaSnimitiNova.setJeAktivno(registracija.getJeAktivno());
						zaSnimitiNova.setRegOd(registracija.getRegOd());
						zaSnimitiNova.setRegDo(registracija.getRegDo());
						
						entityManager.persist(zaSnimitiNova);
						entityManager.flush();
						entityManager.close();
						return registracija;
					} else{	
						return null;
					}
				}
			}else{
				zaSnimitiNova.setVozilo(voziloRepository.findByVin(registracija.getVozilo().getVin()));
				zaSnimitiNova.setOsigOd(registracija.getOsigOd());
				zaSnimitiNova.setOsigDo(registracija.getOsigDo());
				zaSnimitiNova.setJeAktivno(registracija.getJeAktivno());
				zaSnimitiNova.setRegOd(registracija.getRegOd());
				zaSnimitiNova.setRegDo(registracija.getRegDo());
				
				entityManager.persist(zaSnimitiNova);
				entityManager.flush();
				entityManager.close();
				return registracija;
			}

		}else{
			zaSnimitiNova.setVozilo(voziloRepository.findByVin(registracija.getVozilo().getVin()));
			zaSnimitiNova.setOsigOd(registracija.getOsigOd());
			zaSnimitiNova.setOsigDo(registracija.getOsigDo());
			zaSnimitiNova.setJeAktivno(registracija.getJeAktivno());
			zaSnimitiNova.setRegOd(registracija.getRegOd());
			zaSnimitiNova.setRegDo(registracija.getRegDo());

			entityManager.persist(zaSnimitiNova);
			return registracija;
		}
	}
	
	/**
	 * Metoda azurira postojecu registraciju
	 * @param registracija
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Registracija edit(Registracija registracija){
		String prva = "select r from Registration r where r.vehicle.vin = :vin";
		List<Registracija> testNull = new ArrayList<Registracija>();
		Registracija zaSnimitiStara = new Registracija();
		Boolean tester = false;
		try {
			testNull = entityManager.createQuery(prva).setParameter("vin", registracija.getVozilo().getVin()).getResultList();
		} catch (Exception e) {
			
		}
		if(registracija.getJeAktivno()){
			for (Registracija registracija2 : testNull) {
				if(registracija2.getRegOd().after(registracija.getRegOd()))
					tester=true;
			}
			if(tester){
				return null;
			}else{
				zaSnimitiStara.setVozilo(voziloRepository.findByVin(registracija.getVozilo().getVin()));
				zaSnimitiStara.setOsigOd(registracija.getOsigOd());
				zaSnimitiStara.setOsigDo(registracija.getOsigDo());
				zaSnimitiStara.setJeAktivno(registracija.getJeAktivno());
				zaSnimitiStara.setRegOd(registracija.getRegOd());
				zaSnimitiStara.setRegDo(registracija.getRegDo());
				zaSnimitiStara.setId(registracija.getId());

				entityManager.merge(zaSnimitiStara);
				entityManager.flush();
				entityManager.close();
				return registracija;
			}
		}else{
			zaSnimitiStara.setVozilo(voziloRepository.findByVin(registracija.getVozilo().getVin()));
			zaSnimitiStara.setOsigOd(registracija.getOsigOd());
			zaSnimitiStara.setOsigDo(registracija.getOsigDo());
			zaSnimitiStara.setJeAktivno(registracija.getJeAktivno());
			zaSnimitiStara.setRegOd(registracija.getRegOd());
			zaSnimitiStara.setRegDo(registracija.getRegDo());
			zaSnimitiStara.setId(registracija.getId());

			entityManager.merge(zaSnimitiStara);
			entityManager.flush();
			entityManager.close();
			return registracija;
		}
	}
	
	/**
	 * Metoda brise postojecu registraciju
	 * @param id
	 * @return
	 */
	@Transactional
	public Registracija delete(Long id){
		try {
			Registracija zaBrisati = this.read(id);
			entityManager.remove(zaBrisati);
			entityManager.flush();
			return zaBrisati;
		} catch (Exception e) {
			return null;
		}
	}
	
	//***********************************************
	//*					LIST METODE					*
	//*												*
	//***********************************************
	


}

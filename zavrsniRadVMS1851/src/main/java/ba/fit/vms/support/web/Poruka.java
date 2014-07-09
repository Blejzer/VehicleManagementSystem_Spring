package ba.fit.vms.support.web;

/**
 * Poruka koja ce se prikazati u web kontekstu. U zavisnosti od vrste, primijeniti ce se razlicit stil.
 */
public class Poruka {

	/**
     * Naziv flash atributa.
     */
	public static final String MESSAGE_ATTRIBUTE = "poruka";

    /**
     * Vrsta poruke koja ce biti prikazana. Vrsta se koristi da prikaze poruku u razlicitom stilu.
     */
	public static enum Type {
        DANGER, WARNING, INFO, SUCCESS;
	}

	private final String poruka;
	private final Type vrsta;
	private final Object[] args;

	public Poruka(String poruka, Type vrsta) {
		this.poruka = poruka;
		this.vrsta = vrsta;
		this.args = null;
	}
	
	public Poruka(String poruka, Type vrsta, Object... args) {
		this.poruka = poruka;
		this.vrsta = vrsta;
		this.args = args;
	}

	public String getPoruka() {
		return poruka;
	}

	public Type getVrsta() {
		return vrsta;
	}

	public Object[] getArgs() {
		return args;
	}

	
	
}

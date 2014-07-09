package ba.fit.vms.support.web;

import static ba.fit.vms.support.web.Poruka.MESSAGE_ATTRIBUTE;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public final class PorukaHelper {
	
	private PorukaHelper(){};
	
	public static void addSuccessAttribute(RedirectAttributes ra, String poruka, Object... args) {
        addAttribute(ra, poruka, Poruka.Type.SUCCESS, args); 
	}
	
	public static void addErrorAttribute(RedirectAttributes ra, String poruka, Object... args) {
        addAttribute(ra, poruka, Poruka.Type.DANGER, args);
    }

    public static void addInfoAttribute(RedirectAttributes ra, String poruka, Object... args) {
        addAttribute(ra, poruka, Poruka.Type.INFO, args);
    }

    public static void addWarningAttribute(RedirectAttributes ra, String poruka, Object... args) {
        addAttribute(ra, poruka, Poruka.Type.WARNING, args);
    }

    private static void addAttribute(RedirectAttributes ra, String poruka, Poruka.Type vrsta, Object... args) {
        ra.addFlashAttribute(MESSAGE_ATTRIBUTE, new Poruka(poruka, vrsta, args));
    }

    public static void addSuccessAttribute(Model model, String poruka, Object... args) {
        addAttribute(model, poruka, Poruka.Type.SUCCESS, args);
    }

    public static void addErrorAttribute(Model model, String poruka, Object... args) {
        addAttribute(model, poruka, Poruka.Type.DANGER, args);
    }

    public static void addInfoAttribute(Model model, String poruka, Object... args) {
        addAttribute(model, poruka, Poruka.Type.INFO, args);
    }

    public static void addWarningAttribute(Model model, String poruka, Object... args) {
        addAttribute(model, poruka, Poruka.Type.WARNING, args);
    }
	
	private static void addAttribute(Model model, String poruka, Poruka.Type vrsta, Object... args) {
        model.addAttribute(MESSAGE_ATTRIBUTE, new Poruka(poruka, vrsta, args));
    }

}

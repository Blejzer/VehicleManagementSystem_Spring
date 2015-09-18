package ba.fit.vms.pristup;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("userAtribut")
public class ZabranjenPristupController {
	
	 /**
     * Procesuiranje exceptiona zabrane pristupa
     * @return
     */
    @RequestMapping(value = "/auth/zabranjenPristup")
    public String processZabranjenPristupException(){
        return "redirect:/securityAccessDeniedView";
    }

    
    /**
     * redirekcija na stranicu zabrane pristupa
     * @return
     */
    @RequestMapping(value = "/securityAccessDeniedView")
    public String prikaziZabranjenPristupView(){
        return "/auth/zabranjenPristup";
    }

}

package ba.fit.vms.pristup;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

public class ZabranjenPristup extends AccessDeniedHandlerImpl{
	
	@Override
	public void handle(HttpServletRequest _request, HttpServletResponse _response, AccessDeniedException _exception) throws IOException, ServletException {
		setErrorPage("/auth/zabranjenPristup");  // ovo je standardni Spring MVC Controller
		logger.debug("ZabranjenPristup:  Korisnik je pokusao pristupiti resursu za koji nema permisije. Korisnik %s je pokusao pristupiti %s");
		// Svaki put kada korisnik pokusa da pristupi dijelu aplikacije za koji nemaju prava mozemo dodati kastomizirani kod 
		// kojim cemo zakljucati njihov racun
		// <kastomizirani kod za zakljucavanje racuna>
		
		super.handle(_request, _response, _exception);
	}

}

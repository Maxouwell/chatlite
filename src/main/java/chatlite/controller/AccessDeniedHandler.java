package chatlite.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ade)
			throws IOException, ServletException {
		
		if(ade instanceof MissingCsrfTokenException) {
			response.sendRedirect(request.getContextPath() + "/login?csrfError");
		} else {
			response.sendRedirect(request.getContextPath() + "/login?generalError");
		}
	}

}

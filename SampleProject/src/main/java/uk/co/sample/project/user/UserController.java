package uk.co.sample.project.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public class UserController {

	@RequestMapping(value = "/users", method = RequestMethod.GET)
	  public ModelAndView getHomeRedirect(HttpServletRequest aHttpReq) {
	    return new ModelAndView("user/users");
	  }

}

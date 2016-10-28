package uk.co.sample.project;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/")
public class HomeController {

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView getHomePage(HttpServletRequest httpServletRequest) {
		return new ModelAndView("index");
	}
	
	@RequestMapping(value = "home", method = RequestMethod.GET)
	public ModelAndView getHomeRedirect(HttpServletRequest aHttpReq) {
		return getHomePage(aHttpReq);
	}
}

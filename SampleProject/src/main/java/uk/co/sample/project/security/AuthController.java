package uk.co.sample.project.security;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import uk.co.sample.project.user.UserService;

@Controller
public class AuthController {

  @Resource
  Environment environment;

  @Resource
  UserService userService;

  @RequestMapping(value = "login")
  public ModelAndView getLoginPage() {
    if (environment.getRequiredProperty("frontend.security.profile").equals("SSO")) {
      return new ModelAndView("redirect:home");
    }
    return new ModelAndView("/security/login");
  }

  @RequestMapping(value = "changePassword", method = RequestMethod.GET)
  public ModelAndView getChangePasswordPage() {
    if (environment.getRequiredProperty("frontend.security.profile").equals("SSO")) {
      return new ModelAndView("redirect:home");
    }
    return new ModelAndView("security/change_password");
  }

  @RequestMapping(value = "changePassword", method = RequestMethod.POST)
  public ModelAndView changeWebUserPassword(@ModelAttribute ChangePasswordCommand command) {
    String result = userService.changeWebUserPassword(command);
    if (result.startsWith("Success")) {
      userService.setWebUserPasswordNeedsChangeFalse(command.getUsername());
      return new ModelAndView("redirect:login?messageType=success&message=Password changed please login");
    }
    return getChangePasswordPage().addObject("messageType", "error").addObject("message", result.replace("Error", ""));
  }

  @RequestMapping(value = "sessionTimeout")
  public ModelAndView sesstionTimeout() {
    return getLoginPage().addObject("messageType", "error").addObject("message", "Session timed out");
  }

  @RequestMapping(value = "ssoAuthenticationError")
  public ModelAndView getSSOAuthErrorPage(HttpServletRequest httpServletRequest) {
    return new ModelAndView("security/ssoAuthError").addObject("errorMessage",
                                                               httpServletRequest
                                                                 .getSession()
                                                                 .getAttribute("SSO_AUTH_ERROR"));
  }

  @RequestMapping(value = "checkAuth", method = RequestMethod.GET)
  @ResponseBody
  public String checkAuthStatus() {
    String userName = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (userName.equals("anonymousUser")) {
      return "NO";
    }
    return "YES";
  }
  
  @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
  public ModelAndView handleAccessDenied() {
    return new ModelAndView("status.code/403");
  }

  /**
   * Command class used to transfer data from the view.
   */
  public static class ChangePasswordCommand {

    private String username;
    private String password;
    private String newPassword;
    private String confirmNewPassword;

    public String getUsername() {
      return this.username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return this.password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getNewPassword() {
      return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
      this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
      return this.confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
      this.confirmNewPassword = confirmNewPassword;
    }
  }

}

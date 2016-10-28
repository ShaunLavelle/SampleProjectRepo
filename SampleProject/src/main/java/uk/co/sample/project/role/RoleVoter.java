package uk.co.sample.project.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.core.env.Environment;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;

import uk.co.sample.project.domain.Role;
import uk.co.sample.project.domain.Url;
import uk.co.sample.project.domain.User;
import uk.co.sample.project.url.UrlService;
import uk.co.sample.project.user.UserService;

public class RoleVoter implements AccessDecisionVoter {

  @Resource
  UserService userService;

  @Resource
  RoleService roleService;

  @Resource
  UrlService urlService;

  @Resource
  Environment mEnvironment;

  private List<String> securedUrls = new ArrayList<>();

  @PostConstruct
  void populateList() {
    List<Url> urls = urlService.getAllUrl();
    for (Url url : urls) {
      securedUrls.add(url.getUrl());
    }
  }

  @Override
  public boolean supports(ConfigAttribute attribute) {
    return true;
  }

  @Override
  public boolean supports(Class clazz) {
    return true;
  }

  /**
   * This function takes the incomming request and the authentication object and returns a result based on the access
   * that the user has to the page that is being requested.
   *
   * @param authentication the authentication object.
   * @param object         the object to be voted on.
   * @param attributes     the attributes.
   * @return the result of the vote
   */
  @Override
  public int vote(Authentication authentication, Object object, Collection attributes) {
    // don't vote if this is an an unauthenticated user
    if (authentication instanceof AnonymousAuthenticationToken) {
      return ACCESS_GRANTED;
    }

    // get the url that the user is requesting
    FilterInvocation filterInvocation = (FilterInvocation) object;
    String contextPath = filterInvocation.getHttpRequest().getContextPath();
    String requestUrl = filterInvocation
      .getHttpRequest()
      .getRequestURI()
      .replace(contextPath, "")
      .replaceFirst("/", "");
    // see if this is a secured url if not don't vote
    if (!securedUrls.contains(requestUrl)) {
      return ACCESS_ABSTAIN;
    }

    // make sure that we have a user
    String username = authentication.getName();
    if (username.contains("\\")) {
      username = authentication.getName().substring(username.indexOf("\\") + 1, username.length());
    }

    // get the user details out of the cache
    User user = (User) userService.loadUserByUsername(username);
    if (user == null) {
      return ACCESS_ABSTAIN;
    }

    // make sure that the user is active, unlocked, and has a valid password if not then clear the security context
    if (mEnvironment.getRequiredProperty("frontend.security.profile").equals("NOSSO")) {
      if (user.getLocked() || !user.getActive() || user.getNewPasswordRequired()) {
        filterInvocation.getHttpRequest().getSession().invalidate();
      }
    }
    else {
      if (user.getLocked() || !user.getActive()) {
        SecurityContextHolder.clearContext();
      }
    }

    // check to see if the user is allowed to access this url
    for (Role webRole : user.getRoles()) {
      for (Url url : webRole.getAllowedUrls()) {
        if (url.getUrl().equals(requestUrl)) {
          // make a change
          return ACCESS_GRANTED;
        }
      }
    }
    return ACCESS_DENIED;
  }
}

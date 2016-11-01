package uk.co.sample.project.security;

import java.time.LocalDateTime;

import javax.annotation.Resource;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import uk.co.sample.project.domain.User;
import uk.co.sample.project.user.UserService;

@Component
public class SampleProjectAuthenticationProvider implements AuthenticationProvider {

  @Resource
  Environment environment;

  @Resource
  UserService userService;

  @Resource
  PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String username = authentication.getName();
    String password = authentication.getCredentials().toString();
    User user = (User) userService.loadUserByUsername(username);
    if (user == null) {
      throw new BadCredentialsException("Unknown username or password");
    }
    if (user.getEndDate() != null && user.getEndDate().isBefore(LocalDateTime.now())) {
      throw new BadCredentialsException("Unknown username or password");
    }
    if (!user.getActive()) {
      throw new LockedException("The account is no longer active");
    }
    if (user.getLocked()) {
      // If user has been locked for X days, automatically unlock them, if allowed by properties.
      if (environment.getRequiredProperty("frontend.security.automaticallyUnlockAccounts").equals("true")) {
        LocalDateTime unlockableDate = user
          .getLockedDate()
          .plusDays(Integer.parseInt(environment.getRequiredProperty("frontend.security.numberOfDaysToResetAccounts")));
        if (unlockableDate.isBefore(LocalDateTime.now())) {
          userService.setWebUserUnlocked(user.getUsername());
        }
        else {
          throw new LockedException("The account is locked");
        }
      }
      else {
        throw new LockedException("The account is locked");
      }
    }
    if (user.getNewPasswordRequired()) {
      throw new BadCredentialsException("Password needs to be changed");
    }
    if (!passwordEncoder.matches(password, user.getPassword())) {
      if (userService.incrementFailedLoginAttempts(username) >= 
    		  Integer.parseInt(environment.getRequiredProperty("frontend.security.numberOfConsecutiveFailedLoginAllowed"))) {
        userService.setWebUserLocked(username);
        throw new LockedException("Account locked, reached the allowed number of failed login attempts");
      }
      throw new BadCredentialsException("Unknown username or password");
    }
    //make sure that the password is in date and valid
    if (LocalDateTime.now().isAfter(user.getPasswordExpires())) {
      throw new BadCredentialsException("Your password has expired, change your password");
    }
    else {
      userService.resetFailedLoginAttempts(username);
      return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }
}

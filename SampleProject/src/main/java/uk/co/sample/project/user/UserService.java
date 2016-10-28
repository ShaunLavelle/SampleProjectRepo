package uk.co.sample.project.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import org.hibernate.Hibernate;

import uk.co.sample.project.domain.PasswordHistory;
import uk.co.sample.project.domain.Role;
import uk.co.sample.project.domain.User;
import uk.co.sample.project.role.RoleDao;
import uk.co.sample.project.security.AuthController;

@Service
public class UserService implements UserDetailsService {

  @Resource
  Environment environment;

  @Resource
  UserDao userDao;

  @Resource
  RoleDao roleDao;

  @Resource
  TransactionTemplate transactionTemplate;

  @Resource
  PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
    return transactionTemplate.execute(status -> userDao.getUserByUsername(s));
  }

  public User getUserByID(Long Id) {
    return transactionTemplate.execute(status -> userDao.getUserByID(Id));
  }

  public User getUserByUsername(String username) {
    return transactionTemplate.execute(status -> userDao.getUserByUsername(username));
  }

  public void saveNewUser(User user) {
    transactionTemplate.execute(status -> {
      user.setStartDate(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0));
      user.setConsecutiveFailedLogin(0);
      if (user.getPassword() != null) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
      }
      if (user.getPasswordExpires() != null) {
        storePasswordExpieryDate(user, user);
      }
      userDao.saveOrUpdate(user);
      return 1;
    });
    updatePasswordHistory(user);
  }

  public User updateWebUser(User user) {
    return transactionTemplate.execute(status -> {
      User dbUser = userDao.getUserByID(user.getPk());
      if (dbUser == null) {
        return null;
      }
      if (user.getPassword() != null) {
        storePassword(user, dbUser);
      }
      if (user.getPasswordExpires() != null) {
        storePasswordExpieryDate(user, dbUser);
      }
      user.setActive(user.getActive());
      checkUserLockedState(user, dbUser);
      if (user.getNewPasswordRequired() != null) {
        dbUser.setNewPasswordRequired(user.getNewPasswordRequired());
      }
      dbUser.getRoles().clear();
      if (!user.getRoles().isEmpty()) {
        for (Role role : user.getRoles()) {
          dbUser.getRoles().add(roleDao.getById(role.getPk(), Role.class));
        }
      }
      userDao.saveOrUpdate(dbUser);
      return user;
    });
  }

  public User deleteUserByID(Long Id) {
    return transactionTemplate.execute(status -> {
      User user = userDao.getUserByID(Id);
      if (user == null) {
        return null;
      }
      user.setEndDate(LocalDateTime.now());
      userDao.saveOrUpdate(user);
      return user;
    });
  }

  public User setWebUserUnlocked(String username) {
    return transactionTemplate.execute(status -> {
      User user = userDao.getUserByUsername(username);
      user.setLocked(false);
      resetFailedLoginAttempts(user.getUsername());
      user.setLockedDate(null);
      userDao.saveOrUpdate(user);
      return user;
    });
  }

  public User setWebUserLocked(String username) {
    return transactionTemplate.execute(status -> {
      User user = userDao.getUserByUsername(username);
      user.setLocked(true);
      user.setLockedDate(LocalDateTime.now());
      userDao.saveOrUpdate(user);
      return user;
    });
  }

  /**
   * @param changePasswordCommand
   * @return String. Either Error or Success Message.
   */
  public String changeWebUserPassword(AuthController.ChangePasswordCommand changePasswordCommand) {
    return transactionTemplate.execute(status -> {
      // get the web user for the account that we are changing
      User user = userDao.getUserByUsername(changePasswordCommand.getUsername());
      // make sure that we have a user
      if (user == null) {
        return "Error Unknown username or password";
      }
      // check user is active and unlocked
      String checkActiveAndUnlockedErrMsg = checkActiveAndUnlocked(user);
      if (null != checkActiveAndUnlockedErrMsg) {
        return checkActiveAndUnlockedErrMsg;
      }
      // make sure that the supplied current password is correct
      String confirmCurrentPasswordErrMsg = confirmCurrentPassword(changePasswordCommand.getPassword(), user);
      if (null != confirmCurrentPasswordErrMsg) {
        return confirmCurrentPasswordErrMsg;
      }
      // make sure that the new password and confirm match
      String checkNewPasswordsMatchErrMsg = checkNewPasswordsMatch(changePasswordCommand);
      if (null != checkNewPasswordsMatchErrMsg) {
        return checkNewPasswordsMatchErrMsg;
      }
      // make sure that the password is in the correct length range and
      // make sure that the password contains at least 3 different types of:
      // lowercase, uppercase, numbers, special characters
      String checkPasswordLengthAndValidityErrMsg = checkPasswordLengthAndValidity(changePasswordCommand);
      if (null != checkPasswordLengthAndValidityErrMsg) {
        return checkPasswordLengthAndValidityErrMsg;
      }
      // make sure the new password has not already been used. (check password history)
      String errorMessage = "Error Please enter a valid password. Password length " +
                            environment.getRequiredProperty("frontend.security.minPasswordLength") +
                            " and " +
                            environment.getRequiredProperty("frontend.security.maxPasswordLength") +
                            " characters. Includes three of the four types: Lowercase, Uppercase" +
                            ", Numbers or Special Characters. Must not be a previously case sensitive used password in the last " +
                            environment.getRequiredProperty("frontend.security.numberOfPasswordsToKeepInHistory") +
                            ".";
      Hibernate.initialize(user);
      if (passwordExistsInHistory(user, changePasswordCommand.getNewPassword())) {
        return errorMessage;
      }
      user.setPassword(passwordEncoder.encode(changePasswordCommand.getNewPassword()));
      user.setNewPasswordRequired(false);
      user.setPasswordExpires(LocalDateTime
                                .now()
                                .withHour(0)
                                .withMinute(0)
                                .withSecond(0)
                                .withNano(0)
                                .plusDays(Integer.parseInt(environment.getRequiredProperty(
                                  "frontend.security.defaultPasswordExpirationDays"))));
      userDao.saveOrUpdate(user);
      updatePasswordHistory(user);
      return "Password changed successfully";
    });
  }

  public User setWebUserPasswordNeedsChangeFalse(String username) {
    return transactionTemplate.execute(status -> {
      User user = userDao.getUserByUsername(username);
      if (user == null) {
        return null;
      }
      user.setNewPasswordRequired(false);
      userDao.saveOrUpdate(user);
      return user;
    });
  }

  private boolean updatePasswordHistory(User user) {
    return transactionTemplate.execute(status -> {
      // if the password history list is larger than than the number we are holding
      if (!user.getPasswordHistory().isEmpty()) {
        if (user.getPasswordHistory().size() >=
            Integer.parseInt(environment.getRequiredProperty("frontend.security.numberOfPasswordsToKeepInHistory"))) {
          // get the oldest and update it
          // sort the passwords..........ASC
          user
            .getPasswordHistory()
            .sort((PasswordHistory history1, PasswordHistory history2) -> history1
              .getDateSet()
              .compareTo(history2.getDateSet()));

          // gets the potential oldest password and override with newest
          PasswordHistory oldestHistory = user.getPasswordHistory().get(0);
          oldestHistory.setPassword(user.getPassword());
          oldestHistory.setDateSet(LocalDateTime.now());
          user.getPasswordHistory().set(0, oldestHistory);
          userDao.saveOrUpdate(user);
          return true;
        }
        // otherwise just append to the list
        else {
          PasswordHistory passwordHistory = new PasswordHistory(user.getPassword());
          userDao.saveOrUpdate(passwordHistory);
          user.getPasswordHistory().add(new PasswordHistory(user.getPassword()));
          userDao.saveOrUpdate(user);
          return true;
        }
      }
      else {
        PasswordHistory passwordHistory = new PasswordHistory(user.getPassword());
        userDao.saveOrUpdate(passwordHistory);
        user.setPasswordHistory(new ArrayList<>());
        user.getPasswordHistory().add(passwordHistory);
        userDao.saveOrUpdate(user);
        return true;
      }
    });
  }

  private boolean passwordExistsInHistory(User user, String newPassword) {
    return transactionTemplate.execute(s -> {
      // check if this password matches one of the previous passwords
      for (PasswordHistory passwordHistory : user.getPasswordHistory()) {
        if (passwordEncoder.matches(newPassword, passwordHistory.getPassword())) {
          return true;
        }
      }
      return false;
    });
  }

  public Integer incrementFailedLoginAttempts(String username) {
    return transactionTemplate.execute(status -> {
      User user = userDao.getUserByUsername(username);
      user.setConsecutiveFailedLogin(user.getConsecutiveFailedLogin() + 1);
      userDao.saveOrUpdate(user);
      return user.getConsecutiveFailedLogin();
    });
  }

  public User resetFailedLoginAttempts(String username) {
    return transactionTemplate.execute(status -> {
      User user = userDao.getUserByUsername(username);
      if (user.getConsecutiveFailedLogin() > 0) {
        user.setConsecutiveFailedLogin(0);
        userDao.saveOrUpdate(user);
      }
      return user;
    });
  }

  private String checkPasswordLengthAndValidity(AuthController.ChangePasswordCommand changePasswordCommand) {
    String errorMessage = "Error Please enter a valid password. Password length " +
                          environment.getRequiredProperty("frontend.security.minPasswordLength") +
                          " and " +
                          environment.getRequiredProperty("frontend.security.maxPasswordLength") +
                          " characters. Includes three of the four types: Lowercase, Uppercase" +
                          ", Numbers or Special Characters. Must not be a previously case sensitive used password in the last " +
                          environment.getRequiredProperty("frontend.security.numberOfPasswordsToKeepInHistory") +
                          ".";
    Integer minLength = Integer.parseInt(environment.getRequiredProperty("frontend.security.minPasswordLength"));
    Integer maxLength = Integer.parseInt(environment.getRequiredProperty("frontend.security.maxPasswordLength"));
    if (changePasswordCommand.getConfirmNewPassword().length() > maxLength ||
        changePasswordCommand.getNewPassword().length() < minLength) {
      return errorMessage;
    }
    Set<Integer> characterTypes = new HashSet<>();
    for (char c : changePasswordCommand.getNewPassword().toCharArray()) {
      characterTypes.add(Character.getType(c));
    }
    int characterTypeCount = 0;
    // uppercase
    if (characterTypes.contains(1)) {
      characterTypeCount++;
      characterTypes.remove(1);
    }
    // lowercase
    if (characterTypes.contains(2)) {
      characterTypeCount++;
      characterTypes.remove(2);
    }
    // numbers
    if (characterTypes.contains(9)) {
      characterTypeCount++;
      characterTypes.remove(9);
    }
    // anything left is a special character
    if (characterTypes.size() > 0) {
      characterTypeCount++;
    }
    if (characterTypeCount < 3) {
      return errorMessage;
    }
    return null;
  }

  private String checkNewPasswordsMatch(AuthController.ChangePasswordCommand changePasswordCommand) {
    if (changePasswordCommand.getNewPassword() == null ||
        changePasswordCommand.getConfirmNewPassword() == null ||
        !changePasswordCommand.getNewPassword().equals(changePasswordCommand.getConfirmNewPassword())) {
      return "Error The new passwords must match";
    }
    return null;
  }

  private String confirmCurrentPassword(String changePasswordCommand, User user) {
    if (!passwordEncoder.matches(changePasswordCommand, user.getPassword())) {
      // if not increment the failed login and check to see if account needs to be locked
      user.setConsecutiveFailedLogin(user.getConsecutiveFailedLogin() + 1);
      if (user.getConsecutiveFailedLogin() >
          Integer.parseInt(environment.getRequiredProperty("frontend.security.numberOfConsecutiveFailedLoginAllowed")) +
          1) {
        user.setLocked(true);
        return "Error Account locked due to number of failed password " +
               "change attempts please contact the administrator.";
      }
      return "Error Unknown username or password";
    }
    return null;
  }

  private String checkActiveAndUnlocked(User user) {
    // and that they are active
    if (!user.getActive()) {
      return "Error Unable to change password in-active account";
    }
    // and unlocked
    if (user.getLocked()) {
      return "Error Unable to change password for locked account";
    }
    return null;
  }

  private void checkUserLockedState(User user, User dbUser) {
    if (dbUser.getLocked() && !user.getLocked()) {
      setWebUserUnlocked(dbUser.getUsername());
    }
    else if (!dbUser.getLocked() && user.getLocked()) {
      setWebUserLocked(dbUser.getUsername());
    }
  }

  private void storePassword(User user, User dbUser) {
    String hashedPassword = passwordEncoder.encode(user.getPassword());
    dbUser.setPassword(hashedPassword);
    updatePasswordHistory(dbUser);
  }

  private void storePasswordExpieryDate(User user, User dbUser) {
    LocalDateTime passwordExpires = user.getPasswordExpires();
    dbUser.setPasswordExpires(passwordExpires.withHour(0).withMinute(0).withSecond(0).withNano(0));
  }

  public void getByUserAndMpan(User currentlyLoggedInUser, String mpan) {
    // TODO Auto-generated method stub

  }
}

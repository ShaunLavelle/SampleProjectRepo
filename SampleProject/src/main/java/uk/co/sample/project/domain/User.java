package uk.co.sample.project.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "APP_USER")
public class User extends GenericDomain implements UserDetails {

  private String mUsername;
  private String mPassword;
  private LocalDateTime mPasswordExpires;
  private Boolean mActive;
  private Boolean mLocked;
  private LocalDateTime mLockedDate;
  private Boolean mNewPasswordRequired;
  private Integer mConsecutiveFailedLogin;
  private LocalDateTime mStartDate;
  private LocalDateTime mEndDate;
  private List<PasswordHistory> mPasswordHistory = new ArrayList<>();
  private List<Role> mRoles = new ArrayList<>();

  @Column(name = "LOCKED_DATE", nullable = true)
  @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
  public LocalDateTime getLockedDate() {
    return mLockedDate;
  }

  public void setLockedDate(LocalDateTime aLockedDate) {
    mLockedDate = aLockedDate;
  }

  @Column(name = "END_DATE", nullable = true)
  @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
  public LocalDateTime getEndDate() {
    return mEndDate;
  }

  public void setEndDate(LocalDateTime aEndDate) {
    mEndDate = aEndDate;
  }

  @Column(name = "START_DATE", nullable = false)
  @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
  public LocalDateTime getStartDate() {
    return mStartDate;
  }

  public void setStartDate(LocalDateTime aStartDate) {
    mStartDate = aStartDate;
  }

  @Column(name = "USERNAME", nullable = false)
  public String getUsername() {
    return mUsername;
  }

  public void setUsername(String aUsername) {
    mUsername = aUsername;
  }

  @Column(name = "PASSWORD", nullable = false)
  public String getPassword() {
    return mPassword;
  }

  public void setPassword(String aPassword) {
    mPassword = aPassword;
  }

  @Column(name = "PASSWORD_EXPIRES")
  @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
  public LocalDateTime getPasswordExpires() {
    return mPasswordExpires;
  }

  public void setPasswordExpires(LocalDateTime aPasswordExpires) {
    mPasswordExpires = aPasswordExpires;
  }

  @Column(name = "ACTIVE", nullable = false)
  public Boolean getActive() {
    return mActive;
  }

  public void setActive(Boolean aActive) {
    mActive = aActive;
  }

  @Column(name = "LOCKED", nullable = false)
  public Boolean getLocked() {
    return mLocked;
  }

  public void setLocked(Boolean aLocked) {
    mLocked = aLocked;
  }

  @Column(name = "NEW_PASSWORD_REQUIRED", nullable = false)
  public Boolean getNewPasswordRequired() {
    return mNewPasswordRequired;
  }

  public void setNewPasswordRequired(Boolean aNewPasswordRequired) {
    mNewPasswordRequired = aNewPasswordRequired;
  }

  @Column(name = "CONSECUTIVE_FAILED_LOGINS", nullable = false)
  public Integer getConsecutiveFailedLogin() {
    return mConsecutiveFailedLogin;
  }

  public void setConsecutiveFailedLogin(Integer aConsecutiveFailedLogins) {
    mConsecutiveFailedLogin = aConsecutiveFailedLogins;
  }

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "USER_FK")
  public List<PasswordHistory> getPasswordHistory() {
    return mPasswordHistory;
  }

  public void setPasswordHistory(List<PasswordHistory> aPasswordHistory) {
    mPasswordHistory = aPasswordHistory;
  }

  @ManyToMany(cascade = { CascadeType.ALL }, targetEntity = Role.class)
  @JoinTable(name = "ROLE_USER",
             joinColumns = { @JoinColumn(name = "USER_FK") },
             inverseJoinColumns = { @JoinColumn(name = "ROLE_FK") },
             indexes = { @Index(name = "IDX_USER_FK", columnList = "USER_FK"),
                         @Index(name = "IDX_ROLE_FK", columnList = "ROLE_FK") })
  public List<Role> getRoles() {
    return mRoles;
  }

  public void setRoles(List<Role> aRoles) {
    mRoles = aRoles;
  }

  @Transient
  public List<String> getCurrentRoleNames() {
    List<String> roleNames = new ArrayList<>();
    for (Role role : this.getRoles()) {
      roleNames.add(role.getName());
    }
    return roleNames;
  }

  // For the UserDetails interface
  @Override
  @Transient
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getRoles();
  }

  /**
   * For UserDetails Interface.
   *
   * @return if the account is non expired.
   */
  @Override
  @Transient
  public boolean isAccountNonExpired() {
    return getActive();
  }

  /**
   * For UserDetails Interface.
   *
   * @return is the account is non locked.
   */
  @Override
  @Transient
  public boolean isAccountNonLocked() {
    return getActive();
  }

  /**
   * For UserDetails Interface.
   *
   * @return id the credentials are non expired.
   */
  @Override
  @Transient
  public boolean isCredentialsNonExpired() {
    return !getNewPasswordRequired();
  }

  /**
   * For UserDetails Interface.
   *
   * @return if the account is enables.
   */
  @Override
  @Transient
  public boolean isEnabled() {
    return !getLocked();
  }
}

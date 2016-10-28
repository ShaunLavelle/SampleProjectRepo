package uk.co.sample.project.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "PASSWORD_HISTORY", indexes = {@Index(name = "IDX_USER_FK", columnList = "USER_FK")})
public class PasswordHistory extends GenericDomain {

  private String password;
  private LocalDateTime dateSet;

  public PasswordHistory() {
  }

  public PasswordHistory(String password) {
    this.password = password;
    this.dateSet = LocalDateTime.now();
  }

  @Column(name = "PASSWORD", nullable = false)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Column(name = "DATE_SET", nullable = false)
  @Type(type = "org.jadira.usertype.dateandtime.threeten.PersistentLocalDateTime")
  public LocalDateTime getDateSet() {
    return dateSet;
  }

  public void setDateSet(LocalDateTime dateSet) {
    this.dateSet = dateSet;
  }
}

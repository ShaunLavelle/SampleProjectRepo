package uk.co.sample.project.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "ROLE")
public class Role extends GenericDomain implements GrantedAuthority {

  private String name;
  private String description;
  private List<User> users;
  private List<Url> allowedUrls;

  public Role() {
  }

  public Role(String name, String description) {
    this.name = name;
    this.description = description;
  }

  @Column(name = "NAME", nullable = false)
  public String getName() {
    return name;
  }

  public void setName(String aName) {
    name = aName;
  }

  @Column(name = "DESCRIPTION", nullable = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String aDescription) {
    description = aDescription;
  }

  @ManyToMany(mappedBy = "roles")
  public List<User> getUsers() {
    return users;
  }

  public void setUsers(List<User> aUsers) {
    users = aUsers;
  }

  @ManyToMany(cascade = { CascadeType.ALL }, targetEntity = Url.class)
  @JoinTable(name = "URL_ROLE",
             joinColumns = { @JoinColumn(name = "ROLE_FK") },
             inverseJoinColumns = { @JoinColumn(name = "URL_FK") }, indexes = {@Index(name = "IDX_ROLE_FK", columnList = "ROLE_FK"), @Index(name = "IDX_URL_FK", columnList = "URL_FK")})
  public List<Url> getAllowedUrls() {
    return allowedUrls;
  }

  public void setAllowedUrls(List<Url> aAllowedUrls) {
    allowedUrls = aAllowedUrls;
  }

  @Transient
  public List<String> getRoleAllowedUrlNames() {
    List<String> allowedUrlName = new ArrayList<>();
    for (Url url : this.getAllowedUrls()) {
      allowedUrlName.add(url.getUrl());
    }
    return allowedUrlName;
  }

  @Override
  @Transient
  public String getAuthority() {
    return "ROLE_" + getName();
  }
}

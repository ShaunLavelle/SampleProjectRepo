package uk.co.sample.project.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "URL")
public class Url extends GenericDomain {

  private String mUrl;
  private String mDescription;
  private List<Role> mAllowedRoles;

  @Column(name = "URL", nullable = false)
  public String getUrl() {
    return mUrl;
  }

  public void setUrl(String aUrl) {
    mUrl = aUrl;
  }

  @Column(name = "DESCRIPTION", nullable = false)
  public String getDescription() {
    return mDescription;
  }

  public void setDescription(String aDescription) {
    mDescription = aDescription;
  }

  @ManyToMany(mappedBy = "allowedUrls")
  public List<Role> getAllowedRoles() {
    return mAllowedRoles;
  }

  public void setAllowedRoles(List<Role> aAllowedRoles) {
    mAllowedRoles = aAllowedRoles;
  }
}

package uk.co.sample.project.role;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import uk.co.sample.project.domain.Role;
import uk.co.sample.project.domain.Url;
import uk.co.sample.project.url.UrlDao;

@Service
public class RoleService {

  @Resource
  RoleDao roleDao;

  @Resource
  UrlDao urlDao;

  @Resource
  TransactionTemplate transactionTemplate;

  public List<Role> getAllRoles() {
    return transactionTemplate.execute(status -> {
      List<Role> roles = roleDao.getAllRoles();
      for (Role role : roles) {
        Hibernate.initialize(role.getAllowedUrls());
      }
      return roles;
    });
  }

  public Role getRoleByID(Long id) {
    return transactionTemplate.execute(status -> {
      Role role = roleDao.getById(id, Role.class);
      Hibernate.initialize(role.getAllowedUrls());
      return role;
    });
  }

  public Role getRoleByName(String name) {
    return transactionTemplate.execute(status -> roleDao.getRoleByName(name));
  }

  public Role createNewWebRole(String aName, String aDescription) {
    return transactionTemplate.execute(status -> {
      Role role = getRoleByName(aName);
      if (role == null) {
        Role newRole = new Role(aName, aDescription);
        roleDao.saveOrUpdate(newRole);
        return newRole;
      }
      return null;
    });
  }

  public Role updateWebRole(Long id, List<Long> urlIds) {
    return transactionTemplate.execute(status -> {
      Role role = roleDao.getById(id, Role.class);
      if (role == null) {
        return null;
      }
      for (Url url : urlDao.getAllUrl()) {
        role.getAllowedUrls().remove(url);
      }
      if (urlIds != null) {
        for (Long urlId : urlIds) {
          Url url = urlDao.getById(urlId, Url.class);
          role.getAllowedUrls().add(url);
        }
      }
      roleDao.saveOrUpdate(role);
      return role;
    });
  }

  public Boolean deleteWebRole(Long id) {
    return transactionTemplate.execute(status -> {
      Role role = roleDao.getById(id, Role.class);
      if (role.getUsers().size() > 0) {
        return false;
      }
      roleDao.delete(role);
      return true;
    });
  }
}

package uk.co.sample.project.user;

import org.springframework.stereotype.Repository;

import uk.co.sample.project.domain.Role;
import uk.co.sample.project.domain.User;
import uk.co.sample.project.generic.AbstractBasicDao;

import org.hibernate.Hibernate;

@Repository
public class UserDao extends AbstractBasicDao {

  public User getUserByID(Long id) {
    User user = getById(id, User.class);

    if (user != null) {
      Hibernate.initialize(user.getRoles());
    }
    return user;
  }

  public User getUserByUsername(String username) {
    User user = (User) getCurrentSession()
      .createQuery("from User where username = :username")
      .setParameter("username", username)
      .uniqueResult();

    if (user != null) {
      Hibernate.initialize(user.getRoles());
      for (Role role : user.getRoles()) {
        Hibernate.initialize(role.getAllowedUrls());
      }
    }
    return user;
  }

}

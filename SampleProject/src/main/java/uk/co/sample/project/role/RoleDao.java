package uk.co.sample.project.role;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import uk.co.sample.project.domain.Role;
import uk.co.sample.project.generic.AbstractBasicDao;

@Repository
public class RoleDao extends AbstractBasicDao {

	@Resource
	protected SessionFactory sessionFactory;

	public Session getCurrentSession() {
		Session session = sessionFactory.getCurrentSession();
		return session;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getAllRoles() {
		return (List<Role>) getCurrentSession().createQuery("from Role").list();
	}

	public Role getRoleByName(String name) {
		return (Role) getCurrentSession().createQuery("from Role where name = :name").setString("name", name)
				.uniqueResult();
	}
}

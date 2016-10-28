package uk.co.sample.project.generic;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import uk.co.sample.project.domain.GenericDomain;

public abstract class AbstractBasicDao {

	@Resource
	protected SessionFactory sessionFactory;

	public Session getCurrentSession() {
		Session session = sessionFactory.getCurrentSession();
		return session;
	}

	@SuppressWarnings("unchecked")
	public <T> T getById(Long aId, Class<?> aClass) {
		return (T) sessionFactory.getCurrentSession().get(aClass, aId);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getAll(Class<T> aClass) {
		return (List<T>) sessionFactory.getCurrentSession().createCriteria(aClass).list();
	}

	public void saveOrUpdate(GenericDomain genericDomain) {
		getCurrentSession().saveOrUpdate(genericDomain);
	}

	public <T extends GenericDomain> void batchSaveOrUpdate(List<T> genericDomains) {
		Session session = sessionFactory.getCurrentSession();
		int i = 1;
		for (T domain : genericDomains) {
			session.saveOrUpdate(domain);
			if (++i % 100 == 0) {
				session.flush();
			}
		}
		session.flush();
	}

	public void delete(GenericDomain genericDomain) {
		getCurrentSession().delete(genericDomain);
	}
	
	public void batchDelete(List<GenericDomain> genericDomains) {
		Session session = sessionFactory.getCurrentSession();
		int i = 1;
		for (GenericDomain domain : genericDomains) {
			session.delete(domain);
			if (++i % 100 == 0) {
				session.flush();
			}
		}
		session.flush();
	}
	
	public void statelessInsert(GenericDomain genericDomain) {
		sessionFactory.openStatelessSession().insert(genericDomain);
	}

	public void statlessUpdate(GenericDomain genericDomain) {
		sessionFactory.openStatelessSession().update(genericDomain);
	}

	public void statlessDelete(GenericDomain genericDomain) {
		sessionFactory.openStatelessSession().delete(genericDomain);
	}
}

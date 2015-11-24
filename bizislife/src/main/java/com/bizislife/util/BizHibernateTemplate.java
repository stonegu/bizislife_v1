package com.bizislife.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

public class BizHibernateTemplate {
	protected SessionFactory sessionFactory;

	public BizHibernateTemplate (SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	public <T> T get (Class<T> clazz, Serializable key) {
		Session session = sessionFactory.getCurrentSession();
		return (T) session.get(clazz, key);
	}

	public Serializable save (Object o) {
		Session session = sessionFactory.getCurrentSession();
		return session.save(o);
	}

	public void update (Object o) {
		Session session = sessionFactory.getCurrentSession();
		session.update(o);
	}
	public void saveOrUpdate (Object o) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(o);
	}

	public void saveOrUpdateAll (Collection<?> list) {
		if (list==null) return;
		Session session = sessionFactory.getCurrentSession();
		for (Object o : list)
			session.saveOrUpdate(o);
	}

	public void delete (Object o) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(o);
	}

	public void deleteAll (Collection<?> list) {
		Session session = sessionFactory.getCurrentSession();
		for (Object o : list) session.delete(o);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getAll(Class<T> clazz) {
		Session session = sessionFactory.getCurrentSession();
		Criteria crit = session.createCriteria(clazz);
		return crit.list();
	}

	private Integer maxResults; 
	public void setMaxResults (int maxResults) {
		if (maxResults==0)
			this.maxResults = null;
		else
			this.maxResults = new Integer(maxResults);
	}

	public Query createQuery (String queryStr) {
		Session session = sessionFactory.getCurrentSession();
		return session.createQuery(queryStr);
	}

	@SuppressWarnings("rawtypes")
	public List find (String queryStr) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(queryStr);
		if (maxResults!=null) {
			q.setMaxResults(maxResults);
			maxResults = null;
		}
		return q.list();
	}

	@SuppressWarnings("rawtypes")
	public List find (String queryStr, Object... params) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(queryStr);
		if (maxResults!=null) {
			q.setMaxResults(maxResults);
			maxResults = null;
		}
		for (int i=0; i<params.length; i++) q.setParameter(i, params[i]);
		return q.list();
	}

	public int bulkUpdate (String queryStr, Object... params) {
		Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(queryStr);
		for (int i=0; i<params.length; i++) q.setParameter(i, params[i]);
		return q.executeUpdate();
	}

	public Criteria createCriteria (Class<?> clazz) {
		Session session = sessionFactory.getCurrentSession();
		return session.createCriteria(clazz); 
	}
	
	public Object execute(HibernateCallback callback){
		return null;
	}
}
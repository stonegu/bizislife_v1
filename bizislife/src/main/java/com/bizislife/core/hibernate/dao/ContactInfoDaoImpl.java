package com.bizislife.core.hibernate.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.hibernate.pojo.Contactinfo;
import com.bizislife.util.BizHibernateTemplate;

@Repository("contactDao")
//@Transactional
public class ContactInfoDaoImpl implements ContactInfoDao{

    private static final Logger logger = LoggerFactory.getLogger(ContactInfoDaoImpl.class);

    /*
    private BizHibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new BizHibernateTemplate(sessionFactory);
    }
*/
    
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Long saveContact(Contactinfo contact) {
		if(contact!=null && (contact.getAccount_id()!=null || contact.getOrganization_id()!=null)){
//			return (Long)hibernateTemplate.save(contact);
			if(contact.getId()!=null){
				return entityManager.merge(contact).getId();
			}else{
				entityManager.persist(contact);
				return contact.getId();
			}
		}
		
		return null;
	}

	@Override
	public void delContactById(Long id) {
		if(id!=null){
//			Contactinfo contact = hibernateTemplate.get(Contactinfo.class, id);
			Contactinfo contact = entityManager.find(Contactinfo.class, id);
			if(contact!=null){
//				hibernateTemplate.delete(contact);
				entityManager.remove(contact);
			}
		}
	}

	@Override
	public Contactinfo getContactById(Long id) {
		if(id!=null){
//			return hibernateTemplate.get(Contactinfo.class, id);
			return entityManager.find(Contactinfo.class, id);
		}
		return null;
	}

//	@Override
//	public List<Contactinfo> findAccountContactInfosByAccountIds(List<Long> accountIds) {
//		if(accountIds!=null && accountIds.size()>0){
//			// FROM bizislifeii.contactinfo where account_id in (2, 3)
//			StringBuilder q = new StringBuilder("FROM Contactinfo where account_id in (");
//			int size = accountIds.size();
//			for(Long id : accountIds){
//				q.append(id);
//				size--;
//				if(size>0){
//					q.append(",");
//				}
//			}
//			q.append(")");
//			
////			return hibernateTemplate.find(q.toString());
//			Query query = entityManager.createQuery(q.toString());
//			return query.getResultList();
//			
//		}
//		return null;
//	}

    
	@Override
	public List<Contactinfo> findOrgContactsByOrgId(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM Contactinfo where organization_id = ").append(orgId);
//			List<Contactinfo> contacts = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<Contactinfo> findAccountContactsByAccountId(Long accountId) {
		if(accountId!=null){
			StringBuilder q = new StringBuilder("FROM Contactinfo where account_id = ").append(accountId);
//			List<Contactinfo> contacts = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

    
    
    
}

package com.bizislife.core.hibernate.dao;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.bizislife.core.hibernate.pojo.PojoInterface;

@Repository("daoUtil")
public class DaoUtil {
    private static final Logger logger = LoggerFactory.getLogger(DaoUtil.class);
    
	@PersistenceContext
	private EntityManager entityManager;
	
	public<T> Set<T> getRelationshipReferences(Set<T> subSets){
		if(subSets!=null && subSets.size()>0){
			Set<T> refs = new HashSet<T>();
			for(T t : subSets){
				if(PojoInterface.class.isAssignableFrom(t.getClass())){
					if(((PojoInterface)t).getId()!=null){
						T reference = (T)entityManager.getReference(t.getClass(), ((PojoInterface)t).getId());
						if(reference!=null){
							refs.add(reference);
						}
					}else{
						refs.add(t);
					}
				}
			}
			
			if(refs.size()>0) return refs;
		}
		
		return null;
	}
	
	public<T> T getRelationshipReference(T ref){
		if(ref!=null && PojoInterface.class.isAssignableFrom(ref.getClass())){
			
			if(((PojoInterface)ref).getId()!=null){
				return (T)entityManager.getReference(ref.getClass(), ((PojoInterface)ref).getId());
			}else{
				return ref;
			}
		}
		return null;
	}
	

}

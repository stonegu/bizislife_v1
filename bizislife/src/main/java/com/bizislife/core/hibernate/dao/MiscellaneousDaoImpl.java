package com.bizislife.core.hibernate.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.view.dto.NaicDTO;
import com.bizislife.util.BizHibernateTemplate;
import com.bizislife.util.definition.DatabaseRelatedCode;

@Repository("miscellaneousDao")
//@Transactional
public class MiscellaneousDaoImpl implements MiscellaneousDao{
	 
    private static final Logger logger = LoggerFactory.getLogger(MiscellaneousDaoImpl.class);

    /*
    private BizHibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new BizHibernateTemplate(sessionFactory);
    }
*/
    
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
    private Mapper mapper;

	@Override
	public List<NaicDTO> getAllNaics() {
//		List<Naic> naics = hibernateTemplate.find("FROM Naic");
		Query query = entityManager.createQuery("FROM Naic");
		List<Naic> naics = query.getResultList();
		List<NaicDTO> naicDtos = null;
		if(naics!=null && naics.size()>0){
			Map<String, Set<NaicDTO>> tempNaics = new HashMap<String, Set<NaicDTO>>();
			for(Naic n : naics){
				if(tempNaics.get(n.getSupercata().trim())!=null){
					if(n.getCatalevel()!=1){
						tempNaics.get(n.getSupercata().trim()).add(mapper.map(n, NaicDTO.class));
					}
				}else{
					if(n.getCatalevel()!=1){
						Set<NaicDTO> tempSub = new HashSet<NaicDTO>();
						tempSub.add(mapper.map(n, NaicDTO.class));
						tempNaics.put(n.getSupercata().trim(), tempSub);
					}else{
						tempNaics.put(n.getNaicscode().trim(), null);
					}
				}
			}
			
			if(tempNaics.size()>0){
				naicDtos = new ArrayList<NaicDTO>();
				for(Naic n : naics){
					if(n.getCatalevel()==1){
						NaicDTO temp = mapper.map(n, NaicDTO.class);
						temp.setSubNaics(new ArrayList<NaicDTO>(tempNaics.get(n.getNaicscode().trim())));
						naicDtos.add(temp);
					}
				}
				Collections.sort(naicDtos);
			}
		}
		return naicDtos;
	}

	@Override
	public PropertyInTable getPropertyByKey(String key) {
		if(StringUtils.isNotBlank(key)){
//			return hibernateTemplate.get(PropertyInTable.class, key.trim());
			return entityManager.find(PropertyInTable.class, key.trim());
		}
		return null;
	}

	@Override
	public EmailTemplate getEmailTemplateByEmailtype(String type) {
		if(StringUtils.isNotBlank(type)){
			StringBuilder q = new StringBuilder("FROM EmailTemplate where type = '").append(type).append("'");
//			List<EmailTemplate> temps = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<EmailTemplate> temps = query.getResultList();
			if(temps!=null && temps.size()>0){
				return temps.get(0);
			}
		}
		return null;
	}

}

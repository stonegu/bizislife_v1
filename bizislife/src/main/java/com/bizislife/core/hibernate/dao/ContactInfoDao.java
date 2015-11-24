package com.bizislife.core.hibernate.dao;

import java.util.*;

import com.bizislife.core.hibernate.pojo.*;

public interface ContactInfoDao {
	
	public void delContactById(Long id);

	public List<Contactinfo> findAccountContactsByAccountId(Long accountId);
	
	public List<Contactinfo> findOrgContactsByOrgId(Long orgId);
	
	//public List<Contactinfo> findAccountContactInfosByAccountIds(List<Long> accountIds);
	
	public Contactinfo getContactById(Long id);
	
	public Long saveContact(Contactinfo contact);

}

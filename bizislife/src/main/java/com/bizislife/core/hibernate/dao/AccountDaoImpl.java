package com.bizislife.core.hibernate.dao;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.service.PermissionService;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.BizHibernateTemplate;
import com.bizislife.util.definition.DatabaseRelatedCode;

@Repository("accountDao")
//@Transactional
public class AccountDaoImpl implements AccountDao{

    private static final Logger logger = LoggerFactory.getLogger(AccountDaoImpl.class);
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
	
	@Autowired
	private PermissionService permissionService;
	
	@Autowired
	private DaoUtil daoUtil;

	/**
	 * This function is used to transfer pojo to dto 
	 * 
	 * @param account
	 * @return AccountDto
	 */
	public AccountDto transferToDto(Account account){
		AccountDto dto = null;
    	if(account!=null){
    		dto = mapper.map(account, AccountDto.class);
    		if(dto!=null){
        		dto.setSystemDefaultAccount(account.isSystemDefaultAccount());
        		
    			// get org belonging info
    			Organization org = entityManager.find(Organization.class, dto.getOrganization_id());
//    			Organization org = hibernateTemplate.get(Organization.class, dto.getOrganization_id());
    			if(org!=null){
    				dto.setBizAccount(org.getOrglevel()!=null?org.getOrglevel().trim().equals(DatabaseRelatedCode.OrganizationRelated.orglevel_bizislife.getCode())?true:false:false);
    			}
    			
    			// get profile
    			StringBuilder aProfileQ = new StringBuilder("FROM Accountprofile WHERE account_id = ").append(account.getId());
    			Query query = entityManager.createQuery(aProfileQ.toString());
    			List<Accountprofile> aProfiles = query.getResultList();
    			if(aProfiles!=null && aProfiles.size()>0){
    				dto.setFirstname(aProfiles.get(0).getFirstname());
    				dto.setMiddlename(aProfiles.get(0).getMiddlename());
    				dto.setLastname(aProfiles.get(0).getLastname());
    			}
    			
    			// merge permissions
    			Permission mergedPermission = permissionService.getMergedPermissionForAccount(account.getId(), true);
    		
    			dto.setMergedPermission(mergedPermission);
    			
    		}
    	}
    	return dto;
    }
    
    
    @SuppressWarnings("unchecked")
	@Override
	public AccountDto getAccountByUsernameAndPassword(String username, String password) {
		StringBuilder q = new StringBuilder("from Account where loginname = '").append(username).append("' and pwd = '").append(password).append("'");
		try{
//			Account account = (Account)DataAccessUtils.uniqueResult(hibernateTemplate.find(q.toString()));
			Query query = entityManager.createQuery(q.toString());
			List<Account> accounts = query.getResultList();
			if(accounts!=null && accounts.size()>0){
				return transferToDto(accounts.get(0));
			}
			
		}catch (IncorrectResultSizeDataAccessException e) {
            logger.error("More than one account is found by loginname & password");
        }
		return null;
	}

	@Override
	public AccountDto getAccountById(Long accountId) {
		if(accountId!=null){
//			Account account = hibernateTemplate.get(Account.class, accountId);
			Account account = entityManager.find(Account.class, accountId);
			return transferToDto(account);
		}
		return null;
	}

	@Override
	public Account getAccountByUuid(String acctuuid) {
		if(StringUtils.isNotBlank(acctuuid)){
			StringBuilder q = new StringBuilder("FROM Account where accountuuid = '").append(acctuuid).append("'");
//			List<Account> accts = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Account> accts = query.getResultList();
			if(accts!=null && accts.size()>0){
				return accts.get(0);
			}
		}
		return null;
	}


	@Override
	public Account getAccountPojoById(Long accountId) {
		if(accountId!=null){
//			return hibernateTemplate.get(Account.class, accountId);
			return entityManager.find(Account.class, accountId);
		}
		return null;
	}


	@Override
	public List<Account> findAccountsByLoginName(String name) {
		if(StringUtils.isNotBlank(name)){
			StringBuilder q = new StringBuilder("FROM Account where loginname = '").append(name.trim()).append("'");
//			return (List<Account>)hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		
		return null;
	}

	
	@Override
	public Long saveAccount(Account account) {
		// do the basic check
		if(account!=null && account.getOrganization_id()!=null && StringUtils.isNotBlank(account.getLoginname()) && StringUtils.isNotBlank(account.getPwd())){
			if(account.getId()!=null){
//				hibernateTemplate.update(account);
//				return account.getId();
				return entityManager.merge(account).getId();
			}else{
				
				Set<Accountgroup> groups = (Set<Accountgroup>)daoUtil.getRelationshipReferences(account.getAccountGroups());
				account.removeAllGroups();
				if(groups!=null && groups.size()>0){
					for(Accountgroup g : groups){
						account.addAccountGroup(g);
					}
				}
				
				Set<Permission> permissions = (Set<Permission>)daoUtil.getRelationshipReferences(account.getPermissions());
				account.removeAllPermissions();
				if(permissions!=null && permissions.size()>0){
					for(Permission p : permissions){
						account.addPermission(p);
					}
				}

				entityManager.persist(account);
				return account.getId();
			}
		}
		return null;
	}


	@Override
	public Long saveAccountProfile(Accountprofile profile) {
		if(profile!=null && profile.getAccount_id()!=null){
			if(profile.getId()!=null){
//				hibernateTemplate.update(profile);
//				return profile.getId();
				return entityManager.merge(profile).getId();
			}else{
//				return (Long)hibernateTemplate.save(profile);
				entityManager.persist(profile);
				return profile.getId();
			}
		}
		
		return null;
	}


	@Override
	public Accountprofile getAccountProfileByAccountId(Long accountId) {
		if(accountId!=null){
			StringBuilder q = new StringBuilder("FROM Accountprofile where account_id = ").append(accountId);
//			List<Accountprofile> profiles = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Accountprofile> profiles = query.getResultList();
			if(profiles!=null && profiles.size()>0){
				return profiles.get(0);
			}
		}
		
		return null;
	}


	@Override
	public List<Account> findAccountsByIds(String accountIds) {
		if(StringUtils.isNotBlank(accountIds)){
			StringBuilder q = new StringBuilder("FROM Account where id in (").append(accountIds.trim()).append(")");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}


	@Override
	public List<AccountDto> findAccountsInOrg(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM Account where organization_id = ").append(orgId);
//			List<Account> accts = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Account> accts = query.getResultList();
			if(accts!=null && accts.size()>0){
				List<AccountDto> accounts = new ArrayList<AccountDto>();
				for(Account a : accts){
					accounts.add(transferToDto(a));;
				}
				return accounts;
			}
		}
		return null;
	}

}
package com.bizislife.core.service;

import java.util.*;

import javax.jms.JMSException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.configuration.ApplicationConfiguration;
import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.hibernate.dao.*;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.hibernate.pojo.Accountgroup.GroupType;
import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.service.activemq.QueueMsgSender;
import com.bizislife.core.view.dto.*;
import com.bizislife.util.*;
import com.bizislife.util.definition.*;
import com.bizislife.util.validation.ValidationSet;

@Service
public class AccountServiceImpl implements AccountService{

	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
	
	@Autowired
	MessageFromPropertiesService messageFromPropertiesService;
	
    @Autowired
    protected ApplicationConfiguration applicationConfig;

    @Autowired
    private QueueMsgSender queueMsgSender;
    
    @Autowired
    private ActiveMQQueue emailQueue;
    
    @Autowired
	MiscellaneousDao miscellaneousDao;

	@Autowired
	AccountDao accountDao;

	@Autowired
	OrganizationDao orgDao;

	@Autowired
	ContactInfoDao contactDao;
	
	@Autowired
	GroupDao groupDao;
	
	@Autowired
	MessageService messageService;

	@Autowired
	TreeService treeService;

	@Autowired
    private Mapper mapper;

	@Override
	public AccountDto getCurrentAccount() {
		return (AccountDto)WebUtil.getHttpSession().getAttribute(AttributeList.SessionAttribute.loginAccount.name());
	}
	
//	@Override
//	public OrgPaymentProfile getCurrentOrgPaymentProfile(){
//		return (OrgPaymentProfile)WebUtil.getHttpSession().getAttribute(AttributeList.SessionAttribute.orgPaymentProfile.name());
//	}

	@Override
	public void accountLogout() {
		// remove account from session
		WebUtil.getHttpSession().removeAttribute(AttributeList.SessionAttribute.loginAccount.name());
		// remove orgPaymentProfile from session
//		WebUtil.getHttpSession().removeAttribute(AttributeList.SessionAttribute.orgPaymentProfile.name());
	}

	@Override
	@Transactional(readOnly=true)
	public boolean accountCheck(String username, String password) {
		
		if(StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)){
			
			// transfer the password
			//password = password;
			
			// check account in DB
			AccountDto account = accountDao.getAccountByUsernameAndPassword(username, WebUtil.securityPassword(password));
			
			if(account!=null){
				// get org
				Organization org = orgDao.getOrganizationById(account.getOrganization_id());
				
				// check activated for account and org
				if(account.getActivatedate()!=null && org.getActivatedate()!=null){
					// put account into session if account is in DB
					WebUtil.getHttpSession().setAttribute(AttributeList.SessionAttribute.loginAccount.name(), account);
					
//					// put orgPaymentProfile into session
//					OrgPaymentProfile orgPaymentProfile = getOrgPaymentProfile(org.getId());
//					WebUtil.getHttpSession().setAttribute(AttributeList.SessionAttribute.orgPaymentProfile.name(), orgPaymentProfile);
					
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Organization> findOrgsByAccount(Long accountId) {
		
		if(accountId!=null){
			Account account = accountDao.getAccountPojoById(accountId);
			
			if(account.isSystemDefaultAccount()){
				if(isBizIsLifeAccount(null, account)){
					return orgDao.findAllOrgainzations();
				}else{
					List<Organization> oneOrgs = new ArrayList<Organization>();
					oneOrgs.add(orgDao.getOrganizationById(account.getOrganization_id()));
				}
				
			}
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public boolean isBizIsLifeAccount(Long accountId, Account account) {
		if(account!=null || accountId!=null){
			if(account!=null){
				// get account's org
				Organization org = orgDao.getOrganizationById(account.getOrganization_id());
				if(org!=null){
					return org.getOrglevel().equals(DatabaseRelatedCode.OrganizationRelated.orglevel_bizislife.getCode());
				}
			}else {
				// get account by id
				AccountDto acct = accountDao.getAccountById(accountId);
				if(acct!=null){
					Organization org = orgDao.getOrganizationById(acct.getOrganization_id());
					if(org!=null){
						return org.getOrglevel().equals(DatabaseRelatedCode.OrganizationRelated.orglevel_bizislife.getCode());
					}
				}
				
			}
		}
		return false;
	}

	@Override
	@Transactional(readOnly=true)
	public List<GeneralSelectionType> getAllIndustries() {
		List<GeneralSelectionType> naics = null;
		List<NaicDTO> naicDtos = miscellaneousDao.getAllNaics();
		if(naicDtos!=null && naicDtos.size()>0){
			naics = new ArrayList<GeneralSelectionType>();
			for(NaicDTO n : naicDtos){
				naics.add(new GeneralSelectionType(n.getNaicscode(), n.getTitle(), false));
			}
		}
		return naics;
	}

	@Override
	@Transactional(readOnly=true)
	public List<GeneralSelectionType> findSubCatsByIndustryId(String naicsCode) {
		List<GeneralSelectionType> subNaics = null;
		if(StringUtils.isNotBlank(naicsCode)){
			List<NaicDTO> allnaics = miscellaneousDao.getAllNaics();
			for(NaicDTO n : allnaics){
				if(n.getNaicscode().trim().equals(naicsCode.trim())){
					if(n.getSubNaics()!=null && n.getSubNaics().size()>0){
						subNaics = new ArrayList<GeneralSelectionType>();
						for(NaicDTO nd : n.getSubNaics()){
							subNaics.add(new GeneralSelectionType(nd.getNaicscode(), nd.getTitle(), false));
						}
					}
					break;
				}
			}
		}
		return subNaics;
	}

	@Override
	@Transactional(readOnly=true)
	public PropertyInTable getPropertyByKey(String key) {
		if(StringUtils.isNotBlank(key)){
			return miscellaneousDao.getPropertyByKey(key.trim());
		}
		
		return null;
	}

	@Override
	@Transactional
	public void delOrgById(Long id) {
		if(id!=null){
			AccountDto currentAccount = getCurrentAccount();
			if(currentAccount!=null && currentAccount.isSystemDefaultAccount() && (currentAccount.isBizAccount() || currentAccount.getOrganization_id().equals(id))){
				orgDao.delOrgById(id);
			}
		}
	}

	@Override
	@Transactional
	public void delContactById(Long id) {
		if(id!=null){
			AccountDto currentAccount = getCurrentAccount();
			Contactinfo contact = contactDao.getContactById(id);
			
			// check the contactinfo is belonging to account or organization
			boolean isOrgContact = false;
			if(contact.getOrganization_id()!=null){
				isOrgContact = true;
			}
			
			if(isOrgContact){
				if(contact!=null && currentAccount.isSystemDefaultAccount() 
						&& (currentAccount.isBizAccount() || currentAccount.getOrganization_id().equals(contact.getOrganization_id()))){
					contactDao.delContactById(id);
				}
			}else{ // account's contact
				if(contact!=null 
						&& ((currentAccount.isSystemDefaultAccount() && currentAccount.isBizAccount()) || currentAccount.getId().equals(contact.getAccount_id()))){
					contactDao.delContactById(id);
				}
			}
		}
		
	}

	@Override
	@Transactional
	public void delOrgProfileById(Long id) {
		if(id!=null){
			AccountDto currentAccount = getCurrentAccount();
			Organizationprofile profile = orgDao.getOrgProfileById(id);
			
			if(profile!=null && currentAccount!=null && currentAccount.isSystemDefaultAccount() && (currentAccount.isBizAccount() || currentAccount.getOrganization_id().equals(profile.getOrganization_id()))){
				orgDao.delOrgProfileById(id);
			}
			
		}
	}

	@Override
	@Transactional
	public Long newAccount(Account account, String personalEmail, String accountName) {
		// check all the necessary info is in the account
		if(account!=null 
				&& StringUtils.isNotBlank(account.getAccountuuid()) && account.getCreatedate()!=null && account.getCreator_id()!=null
				&& StringUtils.isNotBlank(account.getLoginname()) && account.getOrganization_id()!=null && StringUtils.isNotBlank(account.getPwd())){
			
			Date now = new Date();
			
			// get org by account's orgid
			Organization org = orgDao.getOrganizationById(account.getOrganization_id());
			
			if(org!=null){
				Long accountId =  accountDao.saveAccount(account);
				if(accountId!=null){
					
					if(StringUtils.isNotBlank(personalEmail)){
	    				// *****  send email out
		    			// put account password aside for future process
		    			String accountPassword  = new String(account.getPwd());
	    				// set the MapMessage
	    				ActiveMQMapMessage msg = new ActiveMQMapMessage();
//	    				List<String> emails = new ArrayList<String>();
//	    				emails.add(personalEmail);
	    				Map<String, Map<String, String>> emailsWithArgs = new HashMap<String, Map<String,String>>();
	    				Map<String, String> args = new HashMap<String, String>();
	    				// this emailType is EmailRelated.EmailType.newAccount_activate, you need to pass all the email content arguments to message listener (EmailMessageReceiver.java),
	    				// all the argument's name are list in table emailtemplate's args.
	    				args.put("hostname", applicationConfig.getHostName());
	    				args.put("loginuuid", account.getAccountuuid());
	    				int keepDays = 7;
	    				long endDate = WebUtil.findEndDateByDate(new Date(), keepDays).getTime();
	    				//args.put("sessionExpireDate", Long.toString(endDate));
	    				args.put("loginname", account.getLoginname());
	    				args.put("password", accountPassword);
	    				//args.put("keepdays", Integer.toString(keepDays));
	    				emailsWithArgs.put(personalEmail, args);
	    				try {
							msg.setObject(QueueMsgSender.mapMsgName.emails.name(), emailsWithArgs);
							msg.setString(QueueMsgSender.mapMsgName.emailType.name(), EmailRelated.EmailType.newAccount_activate.name());
						} catch (JMSException e) {
							e.printStackTrace();
							logger.debug(e.toString());
						}
	    				queueMsgSender.send(emailQueue, msg);
	    				
						
					}
					
					// get creator info:
					Accountprofile creator = accountDao.getAccountProfileByAccountId(account.getCreator_id());
					
					// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed
					// 1) log the activity
					String key_oid = "orgId";
					String key_oname = "orgName";
					String key_aid = "accountId";
					String key_aname = "accountName";
					String key_cid = "operatorId";
					String key_cname = "operatorName";
					ActivityLogData activityLogData = new ActivityLogData();
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put(key_oid, org.getId());
						dataMap.put(key_oname, org.getOrgname());
						dataMap.put(key_cid, account.getCreator_id());
						dataMap.put(key_cname, 
								creator!=null?
										(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
										:DatabaseRelatedCode.AccountRelated.accountIdForSystem.getDesc());
						dataMap.put(key_aid, accountId);
						dataMap.put(key_aname, accountName);
						activityLogData.setDataMap(dataMap);
						String desc = messageFromPropertiesService.getMessageSource().getMessage("registerNewAccount", 
								new Object[] { 
									creator!=null?
											(new StringBuilder(creator.getFirstname()).append(" ").append(creator.getLastname()))
											:DatabaseRelatedCode.AccountRelated.accountIdForSystem.getDesc(), 
									accountName}, Locale.US);
						activityLogData.setDesc(desc);
					Long activityLogId = messageService.newActivity(account.getCreator_id(), account.getOrganization_id(), ActivityType.newAcct, activityLogData);
					
					// 2) create a topic
					Topic topic = new Topic(null,
							UUID.randomUUID().toString(),
							new StringBuilder("New account is created in ").append(org.getOrgname()).toString(),
							new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".").append("account.create").toString(),
							Topic.AccessLevel.privateTopic.getCode(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							org.getId(),
							"This topic is used when new account is created",
							now,
							null,
							Topic.TopicType.SystemTopic.getCode(),
							null,
							null,
							null
							);
					Long topicId = messageService.newTopicWithAncestor(topic);
					
					// 3) create a tree node(s)
//					Long treeId = null;
//					if(topicId!=null){
//						treeId = treeService.newTree(topic.getTopicroute(), Tree.TreeCategory.Topic);
//					}
					
					// 4) post to newsfeed
					if(activityLogId!=null){
						// do post ...
						messageService.postFeed(topicId, activityLogId);
						
					}

					return accountId;
				}
				
			}
			
			
		}
		return null;
	}

	@Override
	@Transactional
	public Long newAccountProfile(Accountprofile profile) {
		// check all the necessay info is in the account
		if(profile!=null && profile.getAccount_id()!=null && profile.getCreatedate()!=null){
			return accountDao.saveAccountProfile(profile);
		}
		
		return null;
	}

	@Override
	@Transactional
	public Long newContact(Contactinfo contact) {
		// check all the necessary info is in in contact
		if(contact!=null
				&& (contact.getAccount_id()!=null || contact.getOrganization_id()!=null)
				&& contact.getCreatedate()!=null){
			return contactDao.saveContact(contact);
		}
		
		return null;
	}

	@Override
	@Transactional
	public Long newOrg(Organization org) {
		
		// check all the necessary info is in org
		AccountDto currentAccount = getCurrentAccount();
		if(org!=null 
				&& StringUtils.isNotBlank(org.getOrglevel()) && StringUtils.isNotBlank(org.getOrgname()) && StringUtils.isNotBlank(org.getOrgsysname()) && StringUtils.isNotBlank(org.getOrguuid())
				&& org.getCreatedate()!=null && org.getCreator_id()!=null){
			
			Date now = new Date();
			
			// more other things later ...
			// check orgsysname is exist, system will give a new orgsysname if this name is found in db
			List<Organization> orgsWithSameSystemName = orgDao.findOrgsBySystemName(org.getOrgsysname());
			StringBuilder newOrgSysName = new StringBuilder();
			Random random = new Random();
			while(orgsWithSameSystemName!=null && orgsWithSameSystemName.size()>0){
				newOrgSysName.setLength(0);
				newOrgSysName.append(org.getOrglevel().replace(" ", "_").replaceAll("\'\"", "")).append("(").append(random.nextInt(1000)).append(")");
				org.setOrgsysname(newOrgSysName.toString());
				orgsWithSameSystemName = orgDao.findOrgsBySystemName(org.getOrgsysname());
			}
			
			// pass to dao to save
			Long orgId = orgDao.saveOrg(org);
			if(orgId!=null){
				
				// get creator's info
				String creatorName = null;
				if(org.getCreator_id()>0){
					Accountprofile creatorProfile = accountDao.getAccountProfileByAccountId(org.getCreator_id());
					if(creatorProfile!=null) creatorName = new StringBuilder(creatorProfile.getFirstname()!=null?creatorProfile.getFirstname():"")
						.append(" ")
						.append(creatorProfile.getMiddlename()!=null?creatorProfile.getMiddlename():"")
						.append(" ")
						.append(creatorProfile.getLastname()!=null?creatorProfile.getLastname():"").toString();
				}else{
					DatabaseRelatedCode.AccountRelated defaultAccount = DatabaseRelatedCode.AccountRelated.fromCode(org.getCreator_id().toString());
					if(defaultAccount!=null) creatorName = defaultAccount.getDesc();
					else creatorName = "No Name";
				}
				
				// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) create a tree node(s) by topic route 4) post to accounts newsfeed
				// 1) log the activity:
				String key_oid = "orgId";
				String key_oname = "orgName";
				String key_cid = "operatorId";
				String key_cname = "operatorName";
				ActivityLogData activityLogData = new ActivityLogData();
					Map<String, Object> dataMap = new HashMap<String, Object>();
					dataMap.put(key_oid, orgId);
					dataMap.put(key_oname, org.getOrgname());
					dataMap.put(key_cid, org.getCreator_id());
					dataMap.put(key_cname, creatorName);
					activityLogData.setDataMap(dataMap);
					String desc = messageFromPropertiesService.getMessageSource().getMessage("registerNewOrg", new Object[] { creatorName, org.getOrgname() }, Locale.US);
					activityLogData.setDesc(desc);
				Long activityLogId = messageService.newActivity(org.getCreator_id(), orgId, ActivityType.newOrg, activityLogData);
				
				// 2) create a topic
				Topic topic = new Topic(null,
						UUID.randomUUID().toString(),
						"New organization is created",
						new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append("org.create").toString(),
						Topic.AccessLevel.publicTopic.getCode(),
						Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
						getBizOrg().getId(),
						"This topic is used when new organization is created",
						now,
						null,
						Topic.TopicType.SystemTopic.getCode(),
						null,
						null,
						null
						);
				//Long topicId = messageService.newTopic(topic);
				Long topicId = messageService.newTopicWithAncestor(topic);
				
				// 3) create a tree node(s)
//				Long treeId = null;
//				if(topicId!=null){
//					treeId = treeService.newTree(topic.getTopicroute(), Tree.TreeCategory.Topic);
//				}
				
				// 4) post to newsfeed
				if(activityLogId!=null){
					// do post ...
					messageService.postFeed(topicId, activityLogId);
					
				}
				
				return orgId;
			}
		}
		return null;
	}

	@Override
	@Transactional
	public Long newOrgProfile(Organizationprofile profile) {
		// check all the necessary info is in profile
		if(profile!=null && profile.getCreatedate()!=null && profile.getOrganization_id()!=null){
			return orgDao.saveOrgProfile(profile);
		}
		
		
		return null;
	}

	@Override
	@Transactional
	public Long newOrgMeta(OrgMeta meta) {
		// check all the necessary info is in profile
//		if(profile!=null && profile.getCreatedate()!=null && profile.getOrganization_id()!=null){
//			return orgDao.saveOrgProfile(profile);
//		}
		if(meta!=null && meta.getOrgid()!=null){
			return orgDao.saveOrgMeta(meta);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Account> findAccountsByLoginName(String name) {
		if(StringUtils.isNotBlank(name)){
			return accountDao.findAccountsByLoginName(name);
		}
		
		return null;
	}

	@Override
	@Transactional
	public Long activateOrg(Long orgId) {
		if(orgId!=null){
			AccountDto currentAccount = getCurrentAccount();
			if(currentAccount!=null && currentAccount.isSystemDefaultAccount() && currentAccount.isBizAccount()){
				return orgDao.activateOrg(orgId);
			}
		}
		return null;
	}

//	@Override
//	@Transactional(readOnly=true)
//	public List<Contactinfo> findAccountContactInfosByAccountIds(List<Long> accountIds) {
//		if(accountIds!=null && accountIds.size()>0){
//			return contactDao.findAccountContactInfosByAccountIds(accountIds);
//		}
//		
//		return null;
//	}

	@Override
	@Transactional(readOnly=true)
	public EmailTemplate getEmailTemplateByEmailtype(String type) {
		if(StringUtils.isNotBlank(type) && EmailRelated.EmailType.fromString(type)!=null){
			return miscellaneousDao.getEmailTemplateByEmailtype(type);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Organization> findOrgsByName(String orgName) {
		if(StringUtils.isNotBlank(orgName)){
			return orgDao.findOrgsByName(orgName);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Organization> findAllOrgs() {
		return orgDao.findAllOrgainzations();
	}

	@Override
	@Transactional(readOnly=true)
	public Organization getOrgByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return orgDao.getOrgByUuid(uuid);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public Organizationprofile getOrgProfileByOrgId(Long orgId) {
		if(orgId!=null){
			return orgDao.getOrgProfileByOrgId(orgId);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Contactinfo> findOrgContactsByOrgId(Long orgId) {
		if(orgId!=null){
			return contactDao.findOrgContactsByOrgId(orgId);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<AccountDto> findOrgAccountsByOrgUuid(String orgUuid) {
		if(StringUtils.isNotBlank(orgUuid)){
			// get org by uuid
			Organization org = orgDao.getOrgByUuid(orgUuid.trim());
			if(org!=null){
				AccountDto currentAccount = getCurrentAccount();
				if(currentAccount.isBizAccount() || currentAccount.getOrganization_id().equals(org.getId())){
					List<AccountDto> accounts = accountDao.findAccountsInOrg(org.getId());
					return accounts;
				}
			}
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Accountgroup> findOrgGroupsByOrgUuid(String orgUuid) {
		if(StringUtils.isNotBlank(orgUuid)){
			// get org by uuid
			Organization org = orgDao.getOrgByUuid(orgUuid.trim());
			if(org!=null){
				List<Accountgroup> orgGroups = groupDao.findOrgGroups(org.getId());
				return orgGroups;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Accountgroup> findGlobalGroups(String orgUuid) {
		Organization org = orgDao.getOrgByUuid(orgUuid);
		if(org!=null){
			return groupDao.findGlobalGroups(org.getId());
		}
		return null;
		
	}

	@Override
	@Transactional(readOnly=true)
	public Map<Long, String> getOrgIdNameMap() {
		Map<Long, String> idNameMap = new HashMap<Long, String>();
		
		List<Organization> orgs = orgDao.findAllOrgainzations();
		if(orgs!=null && orgs.size()>0){
			for(Organization o : orgs){
				idNameMap.put(o.getId(), o.getOrgname());
			}
		}
		
		return idNameMap;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Accountgroup> findGroupsByIds(Long[] groupIds) {
		if(groupIds!=null && groupIds.length>0){
			return groupDao.findGroupsByIds(groupIds);
		}
		
		return null;
	}

	@Override
	@Transactional
	public Long newGroup(Accountgroup group) {
		if(group!=null && StringUtils.isNotBlank(group.getAccesslevel()) && StringUtils.isNotBlank(group.getGroupname()) && group.getCreatedate()!=null && 
				group.getCreator_id()!=null && group.getOrganization_id()!=null){
			//Long groupId = null;
			
			return groupDao.saveGroup(group);
			
		}
		return null;
	}

//	@Override
//	@Transactional
//	public void groupVisibleToOrgs(Long groupId, Long[] orgIds) {
//		if(groupId!=null && orgIds!=null && orgIds.length>0){
//			groupDao.groupVisibleToOrgs(groupId, orgIds);
//		}
//	}

	@Override
	@Transactional(readOnly=true)
	public Organization getBizOrg() {
		return orgDao.getBizorg();
	}

	@Override
	@Transactional(readOnly=true)
	public Organization getOrgById(Long id) {
		if(id!=null){
			return orgDao.getOrganizationById(id);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public AccountDto getAccountByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			Account account = accountDao.getAccountByUuid(uuid);
			if(account!=null){
				return accountDao.transferToDto(account);
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public Accountprofile getAccountProfile(Long acctId) {
		if(acctId!=null){
			return accountDao.getAccountProfileByAccountId(acctId);
		}
		
		return null;
	}

	@Override
	@Transactional
	public Long activateAccount(String accountUuid) {
		if(StringUtils.isNotBlank(accountUuid)){
			Account account = accountDao.getAccountByUuid(accountUuid);
			if(account!=null && account.getSuspenddate()==null){
				if(account.getActivatedate()==null){
					account.setActivatedate(new Date());
					accountDao.saveAccount(account);
				}
				return account.getId();
			}
		}
		return null;
	}

	@Override
	@Transactional
	public Long deActivateAccount(String accountUuid) {
		if(StringUtils.isNotBlank(accountUuid)){
			Account account = accountDao.getAccountByUuid(accountUuid);
			if(account!=null){
				if(account.getActivatedate()!=null){
					account.setActivatedate(null);
					accountDao.saveAccount(account);
				}
				return account.getId();
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Accountgroup> findGroupByType(GroupType type, Long orgId) {
		return groupDao.findGroupsByType(type, orgId);
	}

	@Override
	@Transactional(readOnly=true)
	public List<Accountgroup> findPrivateGroups(String orgUuid) {
		Organization org = orgDao.getOrgByUuid(orgUuid);
		if(org!=null){
			return groupDao.findPrivateGroups(org.getId());
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public Accountgroup getGroup(Long id) {
		return groupDao.getGroup(id);
	}

	@Override
	@Transactional
	public void delGroupById(Long id) {
		AccountDto currentAccount = getCurrentAccount();
		Accountgroup group = groupDao.getGroup(id);
    	if(currentAccount!=null && currentAccount.isSystemDefaultAccount() 
    			&& group!=null && currentAccount.getOrganization_id().intValue()==group.getOrganization_id().intValue()
    			&& group.getGrouptype().equals(Accountgroup.GroupType.general.name())){
    		groupDao.delGroupById(id);
    	}

	}

	@Override
	@Transactional(readOnly=true)
	public Accountgroup getEveryoneGroup() {
		Organization bizOrg = orgDao.getBizorg();
		if(bizOrg!=null){
			List<Accountgroup> groups = groupDao.findGroupsByType(Accountgroup.GroupType.Everyone, bizOrg.getId());
			if(groups!=null && groups.size()>0) return groups.get(0);
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Contactinfo> findContactInfosForAccount(Long accountId) {
		if(accountId!=null){
			return contactDao.findAccountContactsByAccountId(accountId);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Accountgroup> findGroupsForAccount(Long accountId) {
		Account account = accountDao.getAccountPojoById(accountId);
		if(account!=null && account.getAccountGroups()!=null && account.getAccountGroups().size()>0){
			List<Accountgroup> accountgroups = new ArrayList<Accountgroup>();
			for(Accountgroup g : account.getAccountGroups()){
				accountgroups.add(mapper.map(g, Accountgroup.class));
			}
			
			Collections.sort(accountgroups);
			
			return accountgroups;
		}
		return null;
		
	}

	@Override
	@Transactional
	public ApiResponse updateAccountByFieldnameValue(String accountUuid, String updatedName, String updatedValue) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = getCurrentAccount();
		Date now = new Date();
		Account account = accountDao.getAccountByUuid(accountUuid);
		
		if(loginAccount!=null && account!=null && StringUtils.isNotBlank(updatedName)){
			
			if((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz system account can do everything 
					|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==account.getOrganization_id().intValue()) // all other org's system account can update their org's accounts
					|| (loginAccount.getAccountuuid().equals(account.getAccountuuid())) // update self account		
			){
				
				if(updatedName.trim().equals("firstName")){
					if(StringUtils.isNotBlank(updatedValue)){
						Accountprofile accountprofile = accountDao.getAccountProfileByAccountId(account.getId());
						if(accountprofile==null){
							accountprofile = new Accountprofile(null, account.getId(), null, null, null, null, null, null, null, null, now);
						}
						
						accountprofile.setFirstname(updatedValue.trim());
						
						accountDao.saveAccountProfile(accountprofile);
						apires.setResponse1(updatedValue);
						apires.setSuccess(true);
						
					}else{
						apires.setResponse1("FirstName can't be empty!");
					}
					
					
				}else if(updatedName.trim().equals("lastName")){
					if(StringUtils.isNotBlank(updatedValue)){
						Accountprofile accountprofile = accountDao.getAccountProfileByAccountId(account.getId());
						if(accountprofile==null){
							accountprofile = new Accountprofile(null, account.getId(), null, null, null, null, null, null, null, null, now);
						}
						accountprofile.setLastname(updatedValue.trim());
						
						accountDao.saveAccountProfile(accountprofile);
						apires.setResponse1(updatedValue);
						apires.setSuccess(true);
						
					}else{
						apires.setResponse1("LastName can't be empty!");
					}
				}else if(updatedName.trim().equals("timezone")){
					// check the timezone:
	    			String[] timezoneIds = TimeZone.getAvailableIDs();
	    			boolean timezoneInclude = ArrayUtils.contains(timezoneIds, updatedValue.trim());
	    			if(timezoneInclude){
	    				Accountprofile accountprofile = accountDao.getAccountProfileByAccountId(account.getId());
	    				if(accountprofile==null){
	    					accountprofile = new Accountprofile(null, account.getId(), null, null, null, null, null, null, null, null, now);
	    				}
	    				if(StringUtils.isNotBlank(updatedValue)){
	    					accountprofile.setTimezone(updatedValue.trim());
	    				}else{
	    					accountprofile.setTimezone(null);
	    				}
	    				accountDao.saveAccountProfile(accountprofile);
	    				
	    				int timezoneOffset = TimeZone.getTimeZone(updatedValue.trim()).getOffset(new Date().getTime());
	    				String offset = String.format("%02d:%02d", Math.abs(timezoneOffset / 3600000), Math.abs((timezoneOffset / 60000) % 60));
	    			    offset = (timezoneOffset >= 0 ? "+" : "-") + offset;					
	    			    String timezoneName = "("+offset+")"+TimeZone.getTimeZone(updatedValue.trim()).getDisplayName();
	    				
	    				apires.setResponse1(timezoneName);
	    				apires.setSuccess(true);
	    				
	    			}else{
	    				apires.setResponse1("Timezone \""+updatedValue.trim()+"\" is not in the timezone list, you may need to refresh the page and try again!");
	    			}
					
				}else if(updatedName.trim().equals("password")){
					if(StringUtils.isNotBlank(updatedValue)){
						account.setPwd(WebUtil.securityPassword(updatedValue));
						accountDao.saveAccount(account);
						apires.setResponse1(updatedValue);
						apires.setSuccess(true);
					}else{
						apires.setResponse1("Password can't be empty!");
					}
				}
				
			}else{
				apires.setResponse1("No permission to update account date!");
			}
			
		}else{
			apires.setResponse1("System error, it can't get account and updatedName from the request. You may need to refresh the page again!");
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse updateContactByFieldnameValue(String orgUuid, String accountUuid, Long contactId, String updatedName, String updatedValue) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(StringUtils.isNotBlank(updatedName)){
			
			Date now = new Date();
			
			Organization org = orgDao.getOrgByUuid(orgUuid);
			Account account = accountDao.getAccountByUuid(accountUuid);
			Contactinfo contact = contactDao.getContactById(contactId);
			
			if(contact==null){
				contact = new Contactinfo();
				contact.setCreatedate(now);
			}
			
			if(contact.getOrganization_id()!=null || contact.getAccount_id()!=null || org!=null || account!=null){
				// set org or account info for contact
				if(contact.getOrganization_id()==null && contact.getAccount_id()==null){
					if(org!=null){
						contact.setOrganization_id(org.getId());
					}else if(account!=null){
						contact.setAccount_id(account.getId());
					}
				}
				
				// set the value:
				if(updatedName.trim().equals("email")){
					
					//validate the email
					boolean emailValid = ValidationSet.isValidEmail(updatedValue.trim());
					if(emailValid){
						contact.setEmail(updatedValue.trim());
						contactDao.saveContact(contact);
						apires.setSuccess(true);
						apires.setResponse1(updatedValue);
					}else{
						apires.setResponse1("Email validation is failed!");
					}
				}
				
				
			}else{
				apires.setResponse1("System can't determine that the contactinfo is for organization or for account. You may need to refresh the page and try again!");
			}
			
		}else{
			apires.setResponse1("No enough information to process the update!");
		}
		
		return apires;
	}

//	@Override
//	@Transactional
//	public ApiResponse updateAccountGroups(String accountuuid, Long[] groupids) {
//		
//		ApiResponse apires = new ApiResponse();
//		apires.setSuccess(false);
//		
//		Account account = accountDao.getAccountByUuid(accountuuid);
//		if(account!=null){
//			
//			List<Accountgroup> groups = new ArrayList<Accountgroup>();
//			if(groupids!=null && groupids.length>0){
//				groups = findGroupsByIds(groupids);
//			}
//			// add everyone group if no everyone group selected!
//			boolean missEveryoneGroup = true;
//			for(Accountgroup g : groups){
//				if(g.getGrouptype().equals(Accountgroup.GroupType.Everyone.name())){
//					missEveryoneGroup = false;
//				}
//			}
//			if(missEveryoneGroup){
//				Accountgroup everyoneGroup = getEveryoneGroup();
//				groups.add(everyoneGroup);
//			}
//
//			
//			// update account's groups
//			Map<Long, Accountgroup> accountgroupsNeedToDel = new HashMap<Long, Accountgroup>();
//			if(account.getAccountGroups()!=null && account.getAccountGroups().size()>0){
//				for(Accountgroup ag : account.getAccountGroups()){
//					accountgroupsNeedToDel.put(ag.getId(), ag);
//				}
//			}
//			
//			// update account's groups
//			boolean accountChanged = false;
//			for(Accountgroup g : groups){
//				boolean findSameGroup = false;
//				if(account.getAccountGroups()!=null && account.getAccountGroups().size()>0){
//					for(Accountgroup ag : account.getAccountGroups()){
//						if(ag.getId().intValue()==g.getId().intValue()){
//							findSameGroup = true;
//							break;
//						}
//					}
//				}
//				if(!findSameGroup){
//					ApiResponse res = joinGroup(g.getId(), account.getId());
//					
////					accountChanged = true;
////					if(account.getAccountGroups()==null){
////						Set<Accountgroup> aGroups = new HashSet<Accountgroup>();
////						account.setAccountGroups(aGroups);
////					}
////					account.getAccountGroups().add(g);
//					
//				}else{
//					accountgroupsNeedToDel.remove(g.getId());
//				}
//			}
//			
//			// remove all groups for account if accountgroupsNeedToDel isn't empty
//			if(accountgroupsNeedToDel.size()>0){
//				accountChanged = true;
//				for(Map.Entry<Long, Accountgroup> agNeedDel : accountgroupsNeedToDel.entrySet()){
//					account.getAccountGroups().remove(agNeedDel.getValue());
//				}
//			}
//			
//			if(accountChanged){
//				accountDao.saveAccount(account);
//			}
//			
//			// get return string
//			StringBuilder returnResult = new StringBuilder();
//			int count = 0;
//			for(Accountgroup g : account.getAccountGroups()){
//				if(count>0){
//					returnResult.append(", ");
//				}
//				returnResult.append(g.getGroupname());
//				count++;
//			}
//			apires.setSuccess(true);
//			apires.setResponse1(returnResult.toString());
//			
//			
//		}else{
//			apires.setResponse1("System can't find account for accountid: "+accountuuid);
//		}
//		
//		return apires;
//	}

	@Override
	@Transactional(readOnly=true)
	public Accountgroup getSystemDefaultGroupInOrg(Long orgId) {
		List<Accountgroup> groups = groupDao.findGroupsByType(Accountgroup.GroupType.SystemDefault, orgId);
		if(groups!=null) return groups.get(0);
		
		return null;
	}

	@Override
	@Transactional
	public ApiResponse updateGroup(Long groupid, String groupname, String description) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = getCurrentAccount();
		
		Accountgroup group = groupDao.getGroup(groupid);
		boolean nameVali = ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(groupname);
		
		if(loginAccount!=null && group!=null){
			
			if((loginAccount.isBizAccount() && loginAccount.isSystemDefaultAccount()) // biz sys account can do anything
				|| (loginAccount.isSystemDefaultAccount() && loginAccount.getOrganization_id().intValue()==group.getOrganization_id().intValue()) // org's sys account can do
			){
				if(nameVali){
					group.setGroupname(groupname);
					group.setDescription(description);
					groupDao.saveGroup(group);
					
					apires.setSuccess(true);
					
				}else{
					apires.setResponse1("Name validation is not passed, name can use a-z, A-Z, 0-9, -, _, space and . only.");
				}
				
			}else{
				apires.setResponse1("You don't have permission to update the group!");
			}
			
		}else{
			apires.setResponse1("System can't find group by groupid: "+groupid);
		}
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public List<OrgCanJoin> findOrgsCanJoin(Long groupId) {

		Accountgroup group = groupDao.getGroup(groupId); 
		
		if(group!=null && group.getOrgCanJoins()!=null && group.getOrgCanJoins().size()>0){
			List<OrgCanJoin> ocjs = new ArrayList<OrgCanJoin>(group.getOrgCanJoins());
			return ocjs;
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<AccountDto> findOrgAccountsInGroup(Long orgId, Long groupId) {
		if(orgId!=null && groupId!=null){
			List<AccountDto> orgAccts = accountDao.findAccountsInOrg(orgId);
			if(orgAccts!=null && orgAccts.size()>0){
				List<AccountDto> acctsInGroup = new ArrayList<AccountDto>();
				
				for(AccountDto a : orgAccts){
					if(a.getAccountGroups()!=null && a.getAccountGroups().size()>0){
						for(Accountgroup g : a.getAccountGroups()){
							if(g.getId().intValue()==groupId.intValue()){
								acctsInGroup.add(a);
								break;
							}
						}
					}
				}
				
				if(acctsInGroup.size()>0) return acctsInGroup;
				
			}
		}
				
		return null;
	}

	@Override
	@Transactional
	public ApiResponse joinGroup(Long groupId, Long accountId, String key) {
		ApiResponse res = new ApiResponse();
		res.setSuccess(false);
		
		Account account = accountDao.getAccountPojoById(accountId);
		Accountgroup group = groupDao.getGroup(groupId);
		
		if(account!=null && group!=null){
			// check if already joined:
			boolean canJoin = true;
			if(account.getAccountGroups()!=null && account.getAccountGroups().size()>0){
				
				for(Accountgroup g : account.getAccountGroups()){
					if(g.getId().intValue()==groupId.intValue()){
						canJoin = false;
						res.setResponse1("account("+account.getAccountuuid()+") already joined group("+group.getGroupname()+")");
						break;
					}
				}
			}
			
			// more check if canJoin is true
			if(canJoin){
				// check if group is outside group, or group is private, but account is not inside org
				if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Global.getCode())){ // if global group
					if(account.getOrganization_id().intValue()!=group.getOrganization_id().intValue()){
						// key check and max # check
						if(group.getOrgCanJoins()!=null && group.getOrgCanJoins().size()>0){
							OrgCanJoin ocj = null;
							for(OrgCanJoin j : group.getOrgCanJoins()){
								if(j.getOrganization_id().intValue()==account.getOrganization_id().intValue()){
									ocj = j;
									break;
								}
							}
							if(ocj!=null){
								if(StringUtils.equals(key, ocj.getJoinkey())){
									
									if(ocj.getTotalaccountcanjoin()!=null && ocj.getTotalaccountcanjoin()>0){
										List<AccountDto> accountsInGroup = findOrgAccountsInGroup(account.getOrganization_id(), group.getId());
										if(accountsInGroup!=null && accountsInGroup.size()>=ocj.getTotalaccountcanjoin().intValue()){
											canJoin = false;
											res.setResponse1("account's organization already reached the maximum number account can join for group ("+group.getGroupname()+")");
										}
										
									}else{
										canJoin = false;
//										res.setResponse1("You need to contact requst sender and to confirm that the totalAccountCanJoin in the System should bigger than 0!")
										res.setResponse1("The total accounts can join for account's organization is 0.");
									}
								}else{
									canJoin = false;
									res.setResponse1("join keys don't match, the key you are using is "+key+", the user is trying to join is "+account.getAccountuuid());
								}
							}else{
								canJoin = false;
								res.setResponse1("Account("+account.getAccountuuid()+") can't join group("+group.getGroupname()+") because group's organization doesn't send invitation to account's organization to join.");
							}
							
						}else {
							canJoin = false;
							res.setResponse1("Account("+account.getAccountuuid()+") can't join group("+group.getGroupname()+") because group's organization doesn't send invitation to account's organization to join.");
						}
					}
				}else if(group.getAccesslevel().equals(Accountgroup.GroupAccessLevel.Private.getCode())){ // if private group
					// only org's account can join private group
					if(account.getOrganization_id().intValue()!=group.getOrganization_id().intValue()){
						canJoin = false;
						res.setResponse1("Only organization's account can join organization's private group.");
					}
				}
				
			}
			
			if(canJoin){
				account.getAccountGroups().add(group);
				Long id = accountDao.saveAccount(account);
				if(id!=null){
					res.setSuccess(true);
					res.setResponse1(mapper.map(group, Accountgroup.class));
				}else{
					res.setResponse1("System doesn't join the group ("+group.getGroupname()+") to account ("+account.getAccountuuid()+")");
				}
			}
			
		}else{
			res.setResponse1("System can't find account and group by accountid: "+accountId+" groupid: "+groupId);
		}
		
		return res;
	}

	@Override
	@Transactional
	public ApiResponse unJoinGroup(Long groupId, Long targetAccountId) {
		ApiResponse res = new ApiResponse();
		res.setSuccess(false);
		
		Account targetAccount = accountDao.getAccountPojoById(targetAccountId);
		Accountgroup targetGroup = groupDao.getGroup(groupId);
		
		if(targetAccount!=null && targetGroup!=null){
			// everyone group can't be unjoined
			if(!targetGroup.getGrouptype().equals(Accountgroup.GroupType.Everyone.name())){
				if(targetAccount.getAccountGroups()!=null && targetAccount.getAccountGroups().size()>0){
					// find group need to be un-join!
					Accountgroup unjoinGroup = null;
					for(Accountgroup g : targetAccount.getAccountGroups()){
						if(g.getId().intValue()==groupId.intValue()){
							unjoinGroup = g;
						}
					}
					if(unjoinGroup!=null){
						targetAccount.getAccountGroups().remove(unjoinGroup);
						Long id = accountDao.saveAccount(targetAccount);
						if(id!=null){
							res.setSuccess(true);
							res.setResponse1(mapper.map(unjoinGroup, Accountgroup.class));
						}else{
							res.setResponse1("System can't unjoin the group, you may need to refresh the page and try again.");
						}
					}
				}
				
			}else{
				res.setResponse1(Accountgroup.GroupType.Everyone.name()+" group can't be un-joined!");
			}
		}else{
			res.setResponse1("System can find account and group by accountid="+targetAccountId+" groupid="+groupId);
		}
		
		return res;
	}

	@Override
	@Transactional(readOnly=true)
	public OrgPaymentProfile getOrgPaymentProfile(Long orgId) {
		OrgPaymentProfile orgPaymentProfile = new OrgPaymentProfile();
		
		// get info from orgMeta
		OrgMeta orgMeta = orgDao.getOrgMeta(orgId);
		if(orgMeta!=null){
//			orgPaymentProfile.setContainersCanHave(orgMeta.getContainersCanHave());
//			orgPaymentProfile.setInstancesCanHave(orgMeta.getInstancesCanHave());
//			orgPaymentProfile.setMaxCharsPerCss(orgMeta.getMaxCharsPerCss());
//			orgPaymentProfile.setMaxCharsPerInstance(orgMeta.getMaxCharsPerInstance());
//			orgPaymentProfile.setMaxCharsPerJsp(orgMeta.getMaxCharsPerJsp());
//			orgPaymentProfile.setMaxCharsPerModuledetail(orgMeta.getMaxCharsPerModuledetail());
//			orgPaymentProfile.setModuledetailsCanHave(orgMeta.getModuledetailsCanHave());
			orgPaymentProfile.setOrgid(orgMeta.getOrgid());
//			orgPaymentProfile.setPagesCanHave(orgMeta.getPagesCanHave());
//			orgPaymentProfile.setProductsCanHave(orgMeta.getProductsCanHave());
		}
		
		return orgPaymentProfile;
	}

	@Override
	@Transactional(readOnly=true)
	public Organization getOrgByHostName(String hostname) {
		if(StringUtils.isNotBlank(hostname)){
			OrgMeta orgMeta = orgDao.getOrgMetaByHostname(hostname);
			if(orgMeta!=null){
				return orgDao.getOrganizationById(orgMeta.getOrgid());
			}else{ 
				// remove "www." from host. (we assume 'www.abc.com' same as 'abc.com')
				if(hostname.startsWith("www.")){
					hostname = hostname.substring(4);
					orgMeta = orgDao.getOrgMetaByHostname(hostname);
					if(orgMeta!=null){
						return orgDao.getOrganizationById(orgMeta.getOrgid());
					}
				}
			}
		}
		return null;
	}

	private boolean domainNameExistForOrg(String domainsInOrgMeta, String domainToCheck){
		
		boolean domainnameExist = false;
		
		if(StringUtils.isNotBlank(domainsInOrgMeta)){
			String[] existDomainsWithStatus = domainsInOrgMeta.split(",");
			for(String dn : existDomainsWithStatus){
				if(domainnameExist) break;
				// remove status info if have
				if(dn.indexOf("[")>-1){
					dn = dn.substring(0, dn.indexOf("["));
				}
				if(dn.equals(domainToCheck)){
					domainnameExist = true;
				}
			}
		}
		
		return domainnameExist;
	}
	
	private ApiResponse domainNameRemoveForOrg(String domainsInOrgMeta, String domainToRemove) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		boolean domainnameExist = false;
		
		if(StringUtils.isNotBlank(domainsInOrgMeta)){
			String[] existDomainsWithStatus = domainsInOrgMeta.split(",");
			int count = 0;
			for(String dn : existDomainsWithStatus){
				if(domainnameExist) break;
				// remove status info if have
				if(dn.indexOf("[")>-1){
					dn = dn.substring(0, dn.indexOf("["));
				}
				if(dn.equals(domainToRemove)){
					domainnameExist = true;
					existDomainsWithStatus[count] = null;
				}
				count++;
			}
			
			if(domainnameExist){
				StringBuilder newDomains = new StringBuilder();
				int i = 0;
				for(String dn : existDomainsWithStatus){
					if(StringUtils.isNotBlank(dn)){
						if(i>0){
							newDomains.append(",");
						}
						newDomains.append(dn);
					}else{
						if(i==0){
							i--;
						}
					}
					i++;
				}
				apires.setSuccess(true);
				apires.setResponse1(newDomains.toString());
			}else{
				apires.setResponse1("No domain '"+domainToRemove+"' in organization");
			}
			
		}else{
			apires.setResponse1("No pre-saved domain in organization");
		}
		
		return apires;
	}
	
	@Override
	@Transactional
	public ApiResponse removeDomainNameForOrg(Long orgId, String domainName) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(orgId!=null && StringUtils.isNotBlank(domainName)){
			domainName = domainName.trim();
			
			OrgMeta orgMeta = orgDao.getOrgMeta(orgId);
			if(orgMeta!=null){
				apires = domainNameRemoveForOrg(orgMeta.getDomains(), domainName);
				if(apires.isSuccess()){
					orgMeta.setDomains((String)apires.getResponse1());
					orgDao.saveOrgMeta(orgMeta);
				}
				
			}else{
				apires.setResponse1("No orgMeta find for org:"+orgId);
			}
		}else{
			apires.setResponse1("There have no enough information to remove a domain");
		}
		
		return apires;
	}
	
	@Override
	@Transactional
	public void saveDomainNameForOrg(Long orgId, String domainName) {
		if(orgId!=null && StringUtils.isNotBlank(domainName)){
			domainName = domainName.trim();
			// domain vali
			if(ValidationSet.isDomain(domainName)){
				OrgMeta orgMeta = orgDao.getOrgMeta(orgId);
				if(orgMeta!=null){
					// check if domainname already exist
					boolean domainnameExist = domainNameExistForOrg(orgMeta.getDomains(), domainName);
					
					if(!domainnameExist){
						if(StringUtils.isNotBlank(orgMeta.getDomains())){
							orgMeta.setDomains(orgMeta.getDomains()+","+domainName+"[pending]");
						}else{
							orgMeta.setDomains(domainName+"[pending]");
						}
						orgDao.saveOrgMeta(orgMeta);
					}
				}
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public OrgMeta getOrgMetaByOrgUuid(String orguuid) {
		Organization org = orgDao.getOrgByUuid(orguuid);
		if(org!=null){
			return orgDao.getOrgMeta(org.getId());
		}
		return null;
	}

	@Override
	@Transactional
	public ApiResponse domainValidateForOrg(String orguuid, String domainName) {
		ApiResponse res = new ApiResponse();
		res.setSuccess(false);
		
		
    	Organization org = orgDao.getOrgByUuid(orguuid);
    	if(org!=null && StringUtils.isNotBlank(domainName)){
        	domainName = domainName.trim();
        	
    		// get domainname's txt code
    		String domainTxtCode = "bizislifeVali="+PasswordUtils.hashPasswordBase64(domainName, org.getSalt());
    		
    		try {
    	    	Hashtable<String, String> env = new Hashtable<String, String>();
    	    	env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
    	    	DirContext dirContext;
    			dirContext = new InitialDirContext(env);
    	    	Attributes attrs = dirContext.getAttributes(domainName, new String[] { "TXT" });
    	    	Attribute txt = attrs.get("TXT");
    	    	if(txt!=null){
    		    	NamingEnumeration e = txt.getAll();
    		    	if(e!=null){
        		    	while (e.hasMore()) {
        		    		String txtInSetup = (String)e.next();
        		    		if(domainTxtCode.equals(txtInSetup.trim())){
        		    			res.setSuccess(true);
        		    			break;
        		    		}
        		    	}
    		    	}
    	    	}
    		} catch (NamingException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		
    		if(!res.isSuccess()){
    			res.setResponse1("Make sure you add TXT record \""+domainTxtCode+"\" to verify domain ownership.");
    		}else{
    			// update the domain status in orgMeta
    			OrgMeta orgMeta = orgDao.getOrgMeta(org.getId());
    			if(orgMeta!=null && StringUtils.isNotBlank(orgMeta.getDomains())){
    				String[] domainsWithStatus = orgMeta.getDomains().split(",");
    				for(int i=0; i<domainsWithStatus.length; i++){
    					
    					String dnWithoutStatus = domainsWithStatus[i];
    					if(domainsWithStatus[i].indexOf("[")>-1){
    						dnWithoutStatus = domainsWithStatus[i].substring(0, domainsWithStatus[i].indexOf("["));
    					}
    					
    					if(dnWithoutStatus.equals(domainName)){
    						domainsWithStatus[i] = dnWithoutStatus;
    					}
    				}
    				StringBuilder updatedDomainWithStatus = new StringBuilder();
    				for(int i=0; i<domainsWithStatus.length; i++){
    					if(i>0){
    						updatedDomainWithStatus.append(",");
    					}
    					updatedDomainWithStatus.append(domainsWithStatus[i]);
    				}
    				orgMeta.setDomains(updatedDomainWithStatus.toString());
    				orgDao.saveOrgMeta(orgMeta);
    			}
    			
    		}
    		
    	}else{
    		res.setResponse1("No enough information to valify the ownership of domain ("+domainName+") for organization ("+orguuid+")");
    	}
		
		return res;
	}

	@Override
	@Transactional
	public ApiResponse suspendAccount(String acctuuid) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		AccountDto loginAccount = getCurrentAccount();
		Account delAccount = accountDao.getAccountByUuid(acctuuid);
		if(loginAccount!=null && delAccount!=null){
			// only admin in org or admin in biz can del account (but can't suspend self
			if(loginAccount.isSystemDefaultAccount() // only admin
				&& loginAccount.getId().longValue()!= delAccount.getId().longValue() // not self
				&& (loginAccount.isBizAccount() || (loginAccount.getOrganization_id().longValue()==delAccount.getOrganization_id().longValue())) // belong to biz or self org
			){
				Date now = new Date();
				
				// 1) set activatedate to null & add date to suspenddate for account
				delAccount.setActivatedate(null);
				delAccount.setSuspenddate(now);
				
				// 2) wrapped loginname with '[[]]' for account and add uuid after loginname
				String newLoginname = "[["+delAccount.getLoginname()+"]]_"+UUID.randomUUID().toString();
				if(newLoginname.length()>100){
					newLoginname.substring(0, 100);
				}
				delAccount.setLoginname(newLoginname);
				
				// save account
				Long delAccountId = accountDao.saveAccount(delAccount);
				
				if(delAccountId!=null){
					apires.setSuccess(true);
					

					Organization org = orgDao.getOrganizationById(delAccount.getOrganization_id());
					Accountprofile delAccountProfile = accountDao.getAccountProfileByAccountId(delAccount.getId());
					
					// 1) log the activity, 2) create a topic (sys.org.create) if no topic exist, 3) post to accounts newsfeed
					// 1) log the activity
					String key_oid = "orgId";
					String key_oname = "orgName";
					String key_aid = "accountId";
					String key_aname = "accountName";
					String key_cid = "operatorId";
					String key_cname = "operatorName";
					ActivityLogData activityLogData = new ActivityLogData();
						Map<String, Object> dataMap = new HashMap<String, Object>();
						dataMap.put(key_oid, org.getId());
						dataMap.put(key_oname, org.getOrgname());
						dataMap.put(key_cid, loginAccount.getId());
						dataMap.put(key_cname, new StringBuilder(loginAccount.getFirstname()).append(" ").append(loginAccount.getLastname()).toString());
						dataMap.put(key_aid, delAccount.getId());
						dataMap.put(key_aname, new StringBuilder(delAccountProfile.getFirstname()).append(" ").append(delAccountProfile.getLastname()).toString());
						activityLogData.setDataMap(dataMap);
						String desc = messageFromPropertiesService.getMessageSource().getMessage("suspendAccount", 
								new Object[] { 
								new StringBuilder(loginAccount.getFirstname()).append(" ").append(loginAccount.getLastname()).toString(), 
								new StringBuilder(delAccountProfile.getFirstname()).append(" ").append(delAccountProfile.getLastname()).toString(),
								org.getOrgname()}, Locale.US);
						activityLogData.setDesc(desc);
					Long activityLogId = messageService.newActivity(loginAccount.getId(), org.getId(), ActivityType.suspendAccount, activityLogData);
					
					// 2) create a topic
					Topic topic = new Topic(null,
							UUID.randomUUID().toString(),
							new StringBuilder("Account is suspended in ").append(org.getOrgname()).toString(),
							new StringBuilder(Tree.TreeRoot.sys.name()).append(".").append(org.getOrgsysname()).append(".").append("account.suspend").toString(),
							Topic.AccessLevel.privateTopic.getCode(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							org.getId(),
							"This topic is used when account is suspended",
							now,
							null,
							Topic.TopicType.SystemTopic.getCode(),
							null,
							null,
							null
							);
					Long topicId = messageService.newTopicWithAncestor(topic);
					
					// 3) create a tree node(s)
//					Long treeId = null;
//					if(topicId!=null){
//						treeId = treeService.newTree(topic.getTopicroute(), Tree.TreeCategory.Topic);
//					}
					
					// 4) post to newsfeed
					if(activityLogId!=null){
						// do post ...
						messageService.postFeed(topicId, activityLogId);
					}
					
				}else{
					apires.setResponse1("System can't delete the account ("+acctuuid+"), you can report this bug or try again later.");
				}
				
				
			}else{
				apires.setResponse1("You don't have permission to delete account.");
			}
		}else{
			apires.setResponse1("System doesn't have enough information to delete account, try refresh and login again.");
		}
		
		return apires;
	}

	@Override
	@Transactional
	public ApiResponse updateOrgInfo(String orguuid, String updatedName, Map<String, String> nameValueMap) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		Organization org = orgDao.getOrgByUuid(orguuid);
		if(org!=null && StringUtils.isNotBlank(updatedName) && nameValueMap!=null && nameValueMap.size()>0){
			if(updatedName.equals("orgname")){ // update org's name
				
				String orgValue = nameValueMap.get("orgname");
				
				if(StringUtils.isNotBlank(orgValue)){
					boolean orgSameNameCheckpassed = true;
					List<Organization> orgsWithTheName = findOrgsByName(orgValue);
					if(orgsWithTheName!=null && orgsWithTheName.size()>0) orgSameNameCheckpassed = false;
					
					boolean orgNameCheckPasssed = true;
					if(StringUtils.containsIgnoreCase(orgValue, "bizislife")){ // can't include 'bizislife'
						orgNameCheckPasssed = false;
					}
					if(orgValue.length()>255){
						orgNameCheckPasssed = false;
					}
					if(!ValidationSet.isAlphaNumUnderscoreDashSpaceDotOnly(orgValue)){
						orgNameCheckPasssed = false;
					}
					
					if(orgSameNameCheckpassed && orgNameCheckPasssed){
						org.setOrgname(orgValue.trim());
						Long orgid = orgDao.saveOrg(org);
						if(orgid!=null){
							apires.setSuccess(true);
							apires.setResponse1(nameValueMap);
							apires.setResponse2(updatedName);
						}else{
							apires.setResponse1("System has issue to save org, you may need to refresh the page and try again!");
						}
					}else{
						StringBuilder errormsg = new StringBuilder();
						if(!orgSameNameCheckpassed){
							errormsg.append("The same organization name has been used.");
						}
						if(!orgNameCheckPasssed){
							errormsg.append(" Make sure that org's name don't include 'bizislife', and small than 255 characters, and only have english characters, numbers, underscores, dashs, spaces, dots.");
						}
						apires.setResponse1(errormsg);
					}
				}
				
			}else if(updatedName.equals("address")){ // update org's address
				
				String zip = nameValueMap.get("zip");
				String address = nameValueMap.get("address");
				String apt_unit_number = nameValueMap.get("apt_unit_number");
				String state = nameValueMap.get("state");
				String street_number = nameValueMap.get("street_number");
				String longitude = nameValueMap.get("longitude");
				String latitude = nameValueMap.get("latitude");
				String city = nameValueMap.get("city");
				String country = nameValueMap.get("country");
				
				if(StringUtils.isNotBlank(address) 
						&& StringUtils.isNotBlank(city) 
						&& StringUtils.isNotBlank(state) 
						&& StringUtils.isNotBlank(country) 
						&& StringUtils.isNotBlank(zip)){
					
					
					if(zip.trim().length()>10) zip = zip.trim().substring(0, 10);
					if(address.trim().length()>255) address = address.trim().substring(0, 255);
					if(apt_unit_number!=null && apt_unit_number.trim().length()>10) apt_unit_number = apt_unit_number.trim().substring(0, 10);
					if(state.trim().length()>255) state = state.trim().substring(0, 255);
					if(street_number!=null && street_number.trim().length()>10) street_number = street_number.trim().substring(0, 10);
					Double longitude_d = null;
					if(longitude!=null){
						longitude_d = Double.valueOf(longitude);
					}
					Double latitude_d = null;
					if(latitude!=null){
						latitude_d = Double.valueOf(latitude);
					}
					if(city.trim().length()>255) city = city.trim().substring(0, 255);
					if(country.trim().length()>20) country = country.trim().substring(0, 20);
					
					List<Contactinfo> contactinfos = contactDao.findOrgContactsByOrgId(org.getId());
					Long contactId = null;
					if(contactinfos==null || contactinfos.size()==0){
						Contactinfo contactinfo = new Contactinfo();
						contactinfo.setOrganization_id(org.getId());
						contactinfo.setCreatedate(new Date());
						
						contactinfo.setApt_unit_number(apt_unit_number);
						contactinfo.setStreet_number(street_number);
						contactinfo.setAddress(address);
						contactinfo.setCity(city);
						contactinfo.setState(state);
						contactinfo.setZip(zip);
						contactinfo.setCountry(country);
						contactinfo.setLatitude(latitude_d);
						contactinfo.setLongitude(longitude_d);
						
						contactId = contactDao.saveContact(contactinfo);
					}else {
						Contactinfo contactinfo = contactinfos.get(0); // support 1 contact one org now
						contactinfo.setApt_unit_number(apt_unit_number);
						contactinfo.setStreet_number(street_number);
						contactinfo.setAddress(address);
						contactinfo.setCity(city);
						contactinfo.setState(state);
						contactinfo.setZip(zip);
						contactinfo.setCountry(country);
						contactinfo.setLatitude(latitude_d);
						contactinfo.setLongitude(longitude_d);
						
						contactId = contactDao.saveContact(contactinfo);
					}
					
					if(contactId!=null){
						apires.setSuccess(true);
						apires.setResponse1(nameValueMap);
						apires.setResponse2(updatedName);

					}else{
						apires.setResponse1("System has issue to save org, you may need to refresh the page and try again!");
					}
					
				}else{
					apires.setResponse1("Address, city, state, country and zip can be empty!");
				}
				
			}else if(updatedName.equals("phone")){ // update org's phone
				String dayphone_country = nameValueMap.get("dayphone_country");
				String dayphone_area = nameValueMap.get("dayphone_area");
				String dayphone = nameValueMap.get("dayphone");
				
				List<Contactinfo> contactinfos = contactDao.findOrgContactsByOrgId(org.getId());
				Long contactId = null;
				if(contactinfos==null || contactinfos.size()==0){
					Contactinfo contactinfo = new Contactinfo();
					contactinfo.setOrganization_id(org.getId());
					contactinfo.setCreatedate(new Date());
					
					contactinfo.setDayphone_country(dayphone_country);
					contactinfo.setDayphone_area(dayphone_area);
					contactinfo.setDayphone(dayphone);
					
					contactId = contactDao.saveContact(contactinfo);
				}else {
					Contactinfo contactinfo = contactinfos.get(0); // support 1 contact one org now
					contactinfo.setDayphone_country(dayphone_country);
					contactinfo.setDayphone_area(dayphone_area);
					contactinfo.setDayphone(dayphone);
					
					contactId = contactDao.saveContact(contactinfo);
				}
				
				if(contactId!=null){
					apires.setSuccess(true);
					apires.setResponse1(nameValueMap);
					apires.setResponse2(updatedName);

				}else{
					apires.setResponse1("System has issue to save org, you may need to refresh the page and try again!");
				}
				
				
				
				
			}else if(updatedName.equals("desc")){ // update org's desc
				
				Organizationprofile orgprofile = orgDao.getOrgProfileByOrgId(org.getId());
				if(orgprofile==null){
					orgprofile = new Organizationprofile();
					orgprofile.setOrganization_id(org.getId());
					orgprofile.setCreatedate(new Date());
				}
				
				orgprofile.setDescription(nameValueMap.get("description"));
				
				Long profileId = orgDao.saveOrgProfile(orgprofile);
				
				if(profileId!=null){
					apires.setSuccess(true);
					apires.setResponse1(nameValueMap);
					apires.setResponse2(updatedName);

				}else{
					apires.setResponse1("System has issue to save org, you may need to refresh the page and try again!");
				}
				
			}
			
		}else {
			apires.setResponse1("No enough information to process updating org info.");
		}
		
		return apires;
	}

}

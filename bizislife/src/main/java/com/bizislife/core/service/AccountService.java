package com.bizislife.core.service;

import java.util.*;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.view.dto.*;

public interface AccountService {
	
    /**
     * this method is used for initializing the db during the development.
     * this method should be disabled when go to live.
     */
//    public void initialDb();
    
    
    
    public boolean accountCheck(String username, String password);
    
    public void accountLogout();
    
    public Long activateOrg(Long orgId);
    
    public Long activateAccount(String accountUuid);
    
    public Long deActivateAccount(String accountUuid);
    
    /**
     * only admin in org or admin in bizislife can suspend account, 
     * but can't suspend self. <br/> 
     * 
     * two actions will be done for the account:<br/>
     * 1) set activatedate to null & add date to suspenddate for account<br/>
     * 2) wrapped loginname with '[[]]' for account and add uuid after loginname<br/>
     * 
     * @param acctuuid
     * @return
     */
    public ApiResponse suspendAccount(String acctuuid);
    
    public void delContactById(Long id);
    
    public void delGroupById(Long id);

    public void delOrgById(Long id);
    
    public void delOrgProfileById(Long id);
    
    public List<Account> findAccountsByLoginName(String name);
    
    //public List<Contactinfo> findAccountContactInfosByAccountIds(List<Long> accountIds);
    
    /**
     * @param type
     * @param orgId
     * @return list of groups by type in organization. if orgId is null, then return list of groups by type.
     */
    public List<Accountgroup> findGroupByType(Accountgroup.GroupType type, Long orgId);
    
    public List<Accountgroup> findGroupsForAccount(Long accountId);
    
    public List<Organization> findAllOrgs();
    
    public List<Contactinfo> findContactInfosForAccount(Long accountId);
    
    /**
     * @param orgUuid
     * @return all global groups for all organizations if orgUuid is null. <br/>
     * return all global groups for organization if orgUuid is not null.
     */
    public List<Accountgroup> findGlobalGroups(String orgUuid);
    
    public List<Accountgroup> findGroupsByIds(Long[] groupIds);
    
    public List<OrgCanJoin> findOrgsCanJoin(Long groupId);
    
    public List<Accountgroup> findOrgGroupsByOrgUuid(String orgUuid);
    
    /**
     * @param orgUuid
     * @return org's accounts. login as biz's account can return all org's accounts. Login as other account can return his/her org's accounts
     */
    public List<AccountDto> findOrgAccountsByOrgUuid(String orgUuid);
    
    public List<AccountDto> findOrgAccountsInGroup(Long orgId, Long groupId);

    public List<Contactinfo> findOrgContactsByOrgId(Long orgId);

    /**
     * @param accountId
     * @return list of organizations base on account's permission:
     * 1. systemDefault account belongs to bizislife - return all organizations
     * 2. systemDefault account belongs to other - return account's organization
     */
    public List<Organization> findOrgsByAccount(Long accountId);
    
    public List<Organization> findOrgsByName(String orgName);
    
    /**
     * @param orgUuid
     * @return all private groups for all organizations if orgUuid is null. <br/>
     * return all private groups for organization if orgUuid is not null.
     */
    public List<Accountgroup> findPrivateGroups(String orgUuid);
    
    public List<GeneralSelectionType> findSubCatsByIndustryId(String naicsCode);
    
    public ApiResponse domainValidateForOrg(String orguuid, String domainName);
    
    public AccountDto getAccountByUuid(String uuid);
    
    public Accountprofile getAccountProfile(Long acctId);
    
    /**
     * @return all industries (top level) from Naics table
     */
    public List<GeneralSelectionType> getAllIndustries();
	
    public Organization getBizOrg();
    
    /**
     * @return account from session
     */
    public AccountDto getCurrentAccount();
    
//    public OrgPaymentProfile getCurrentOrgPaymentProfile();
    
    public EmailTemplate getEmailTemplateByEmailtype(String type);
    
    public Accountgroup getEveryoneGroup();
    
    public Accountgroup getGroup(Long id);
    
    public Organization getOrgByHostName(String hostname);
    
    public Organization getOrgById(Long id);
    
    public Organization getOrgByUuid(String uuid);
    
    public Map<Long, String> getOrgIdNameMap();
    
    public OrgMeta getOrgMetaByOrgUuid(String orguuid);
    
    public OrgPaymentProfile getOrgPaymentProfile(Long orgId);
    
    public Organizationprofile getOrgProfileByOrgId(Long orgId);
    
    public PropertyInTable getPropertyByKey(String key);
    
    public Accountgroup getSystemDefaultGroupInOrg(Long orgId);
    
//    public void groupVisibleToOrgs(Long groupId, Long[] orgIds);
    
    /**
     * user can provide either accountId or account for the check. account has the high priority.
     * @param accountId (optional)
     * @param account (optional)
     * @return
     */
    public boolean isBizIsLifeAccount(Long accountId, Account account);
    
    /**
     * @param groupId group to join
     * @param accountId account to join
     * @param key joinKey for join outside group
     * @return
     */
    public ApiResponse joinGroup(Long groupId, Long accountId, String key);
    
    /**
     * @param account
     * @param personalEmail sent email out if this param is not null/empty
     * @param accountName new account profile name (firstname lastname)
     * @return
     */
    public Long newAccount(Account account, String personalEmail, String accountName);
    
    public Long newAccountProfile(Accountprofile profile);
    
    public Long newContact(Contactinfo contact);
    
    public Long newGroup(Accountgroup group);
    
    public Long newOrg(Organization org);
    
    public Long newOrgMeta(OrgMeta orgMeta);
    
    public Long newOrgProfile(Organizationprofile profile);
    
    public ApiResponse removeDomainNameForOrg(Long orgId, String domainName);
    
    public void saveDomainNameForOrg(Long orgId, String domainName);
    
    public ApiResponse unJoinGroup(Long groupId, Long targetAccountId);
    
    public ApiResponse updateAccountByFieldnameValue(String accountUuid, String updatedName, String updatedValue);
    
    public ApiResponse updateGroup(Long groupid, String groupname, String description);
    
//    public ApiResponse updateAccountGroups(String accountuuid, Long[] groupids);
    
    /**
     * @param orgUuid : if orgUuid isn't null, updating is for org's contactinfo.
     * @param accountUuid : if accountUuid isn't null, updating is for account's contactinfo.
     * @param contactId : if contactId isn't null, updating is for this contactInfo.
     * @param updatedName 
     * @param updatedValue
     * @return
     */
    public ApiResponse updateContactByFieldnameValue(String orgUuid, String accountUuid, Long contactId, String updatedName, String updatedValue);
    
    public ApiResponse updateOrgInfo(String orguuid, String updatedName, Map<String, String>nameValueMap);
    
}

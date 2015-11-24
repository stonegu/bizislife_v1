package com.bizislife.core.view.dto;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.util.definition.DatabaseRelatedCode;

public class AccountDto implements Comparable<AccountDto>, Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;

	private String loginname;
	
	private String firstname;
	private String middlename;
	private String lastname;

	private String pwd;
	
	private String accountuuid;
    
	private Date createdate;
    
	private Date activatedate;
    
	private Date suspenddate;
    
	private boolean bizAccount;
    
	private Long organization_id;
	
	private Long creator_id;
	
	private Set<Accountgroup> accountGroups;
	
	/**
	 * the permission merged all permissions from account and groups
	 * the permission's startdate is now, enddate is the closest enddate in all merged permissions. The purpose for endate can used for refresh merged permissions when endate.before(now).
	 * note: all permissions' startdate after now will not be merged!
	 */
	private Permission mergedPermission;
	
	private boolean systemDefaultAccount;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLoginname() {
		return loginname;
	}

	public void setLoginname(String loginname) {
		this.loginname = loginname;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getAccountuuid() {
		return accountuuid;
	}

	public void setAccountuuid(String accountuuid) {
		this.accountuuid = accountuuid;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Date getActivatedate() {
		return activatedate;
	}

	public void setActivatedate(Date activatedate) {
		this.activatedate = activatedate;
	}

	public Date getSuspenddate() {
		return suspenddate;
	}

	public void setSuspenddate(Date suspenddate) {
		this.suspenddate = suspenddate;
	}

	public boolean isBizAccount() {
		return bizAccount;
	}

	public void setBizAccount(boolean bizAccount) {
		this.bizAccount = bizAccount;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public Long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Long creator_id) {
		this.creator_id = creator_id;
	}

	public Set<Accountgroup> getAccountGroups() {
		return accountGroups;
	}

	public void setAccountGroups(Set<Accountgroup> accountGroups) {
		this.accountGroups = accountGroups;
	}

	public boolean isSystemDefaultAccount() {
		return systemDefaultAccount;
	}

	public void setSystemDefaultAccount(boolean systemDefaultAccount) {
		this.systemDefaultAccount = systemDefaultAccount;
	}
	
	public Permission getMergedPermission() {
		return mergedPermission;
	}

	public void setMergedPermission(Permission mergedPermission) {
		this.mergedPermission = mergedPermission;
	}

	@Override
	public int compareTo(AccountDto that) {
		int i = organization_id.compareTo(that.organization_id);
		if(i!=0) return i;
		return loginname.compareTo(that.loginname);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AccountDto [id=");
		builder.append(id);
		builder.append(", loginname=");
		builder.append(loginname);
		builder.append(", firstname=");
		builder.append(firstname);
		builder.append(", middlename=");
		builder.append(middlename);
		builder.append(", lastname=");
		builder.append(lastname);
		builder.append(", pwd=");
		builder.append(pwd);
		builder.append(", accountuuid=");
		builder.append(accountuuid);
		builder.append(", createdate=");
		builder.append(createdate);
		builder.append(", activatedate=");
		builder.append(activatedate);
		builder.append(", suspenddate=");
		builder.append(suspenddate);
		builder.append(", bizAccount=");
		builder.append(bizAccount);
		builder.append(", organization_id=");
		builder.append(organization_id);
		builder.append(", creator_id=");
		builder.append(creator_id);
		builder.append(", accountGroups=");
		builder.append(accountGroups);
		builder.append(", systemDefaultAccount=");
		builder.append(systemDefaultAccount);
		builder.append("]");
		return builder.toString();
	}

}

package com.bizislife.core.controller.component;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("privilege")
public class Privilege implements Serializable { // hold information for ownership (org, group, account)
	@XStreamOmitField
	private static final long serialVersionUID = -2981275706834076593L;
	private Long orgId;
	private Long groupId;
	private Long accountId;
	public Privilege(Long orgId, Long groupId, Long accountId) {
		super();
		this.orgId = orgId;
		this.groupId = groupId;
		this.accountId = accountId;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("orgId",orgId).append("groupId",groupId).append("accountId",accountId).toString();
    }
	
}

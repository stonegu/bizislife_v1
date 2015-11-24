package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

public class OrgVisibleGroup implements Serializable{
	
	private static final long serialVersionUID = 7252339823011650388L;
	private Long accountgroup_id;
	private Long visibleOrgs_id;
	public Long getAccountgroup_id() {
		return accountgroup_id;
	}
	public void setAccountgroup_id(Long accountgroup_id) {
		this.accountgroup_id = accountgroup_id;
	}
	public Long getVisibleOrgs_id() {
		return visibleOrgs_id;
	}
	public void setVisibleOrgs_id(Long visibleOrgs_id) {
		this.visibleOrgs_id = visibleOrgs_id;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountgroup_id == null) ? 0 : accountgroup_id.hashCode());
		result = prime * result
				+ ((visibleOrgs_id == null) ? 0 : visibleOrgs_id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrgVisibleGroup other = (OrgVisibleGroup) obj;
		if (accountgroup_id == null) {
			if (other.accountgroup_id != null)
				return false;
		} else if (!accountgroup_id.equals(other.accountgroup_id))
			return false;
		if (visibleOrgs_id == null) {
			if (other.visibleOrgs_id != null)
				return false;
		} else if (!visibleOrgs_id.equals(other.visibleOrgs_id))
			return false;
		return true;
	}

}

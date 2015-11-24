package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.bizislife.util.definition.DatabaseRelatedCode;

@Entity
@Table(name="account")
public class Account implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = 4732210448716967278L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="loginname")
	private String loginname;

	@Column(name="pwd")
	private String pwd;
	
	@Column(name="salt")
	private String salt;
	
	@Column(name="accountuuid")
	private String accountuuid;
    
	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="activatedate")
	private Date activatedate;
    
	@Column(name="suspenddate")
	private Date suspenddate;
    
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="creator_id")
	private Long creator_id;
	
	@ManyToMany(cascade = CascadeType.ALL)
	private Set<Accountgroup> accountGroups;	
	
	@OneToMany(orphanRemoval=true, cascade = CascadeType.ALL)
	private Set<Permission> permissions;
	
	public Account() {
		super();
	}

	public Account(Long id, String loginname, String pwd, String accountuuid, 
			Date createdate, Date activatedate, Date suspenddate,
			Long organization_id, Long creator_id,
			Set<Accountgroup> accountGroups,
			Set<Permission> permissions) {
		super();
		this.id = id;
		this.loginname = loginname;
		this.pwd = pwd;
		this.accountuuid = accountuuid;
		this.createdate = createdate;
		this.activatedate = activatedate;
		this.suspenddate = suspenddate;
		this.organization_id = organization_id;
		this.creator_id = creator_id;
		this.accountGroups = accountGroups;
		this.permissions = permissions;
	}

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

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
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

	private void setAccountGroups(Set<Accountgroup> accountGroups) {
		this.accountGroups = accountGroups;
	}
	
	public void addAccountGroup(Accountgroup accountGroup){
		if(this.accountGroups == null) this.accountGroups = new HashSet<Accountgroup>();
		
		this.accountGroups.add(accountGroup);
	}
	
	public void removeAllGroups(){
		if(this.accountGroups!=null && this.accountGroups.size()>0)
			this.accountGroups.clear();
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	private void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
	
	public void addPermission(Permission permission){
		if(this.permissions==null) this.permissions = new HashSet<Permission>();
		this.permissions.add(permission);
	}
	
	public void removeAllPermissions(){
		if(this.permissions!=null && this.permissions.size()>0)
			this.permissions.clear();
	}

	public boolean isSystemDefaultAccount(){
		if(accountGroups!=null && accountGroups.size()>0){
			for(Accountgroup g : accountGroups){
				if(StringUtils.isNotBlank(g.getGrouptype()) && g.getGrouptype().equals(Accountgroup.GroupType.SystemDefault.name())){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("name", loginname).toString();
    }

}

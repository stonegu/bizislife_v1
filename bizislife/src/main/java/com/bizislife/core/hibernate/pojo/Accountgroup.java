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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.dozer.Mapping;

@Entity
@Table(name="accountgroup")
public class Accountgroup implements PojoInterface, Comparable<Accountgroup>, Serializable{

	private static final long serialVersionUID = -8090591381965427006L;
	
	
	public static enum GroupType {
		// the systemDefault group is the group has full privilege in each organization, user can't modify the privilege but can modify the group's prettyname.
		// the first user in systemDefault group is the user to apply new organization.
		// note: one org has one SystemDefault group.
		SystemDefault(false, false),
		
		// everyone group is the group belonging to bizislife (but the accessLevel is Global(1)), which can be used for all organizations. 
		// There will no any privilege preset for this type of group. Each organization can apply different permission for the group.
		// every account must belongs to this Everyone group.
		// note: there is only one Everyone group in whole system.
		Everyone(false, true),
		
		// general group is the group created by org's system person
		general(true, true),
		;
		
		private boolean deleteable;
		private boolean permissionEditable; // can set data permission on it. 
		
		private GroupType(boolean deleteable, boolean permissionEditable) {
			this.deleteable = deleteable;
			this.permissionEditable = permissionEditable;
		}
		
		public static GroupType getTypeFromName(String name){
			if(StringUtils.isNotBlank(name)){
				for(GroupType t : GroupType.values()){
					if(t.name().equals(name)){
						return t;
					}
				}
			}
			return null;
		}

		public boolean isDeleteable() {
			return deleteable;
		}

		public boolean isPermissionEditable() {
			return permissionEditable;
		}

	}
	
	public static enum GroupAccessLevel {

		// only can accept accounts within organization.
		Private("0", "Only organization's accounts can belong to the group"),
		
		// the permission level can be used on global group is <=1. This means not all permissions can be set on global group.
		// only the global level group can accept accounts not belonging to organization.
		Global("1", "All organizations' accounts can belong to the group."),
		
		;
		
		private String code;
		private String desc;

		private GroupAccessLevel(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}
		
	}

	@Id
	@GeneratedValue
	@Column(name="id")
	private Long id;
	
	@Column(name="uuid")
	private String uuid;

	@Column(name="groupname")
	private String groupname;

	@Column(name="accesslevel")
	private String accesslevel;

	@Column(name="description")
	private String description;

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
	
	@Column(name="grouptype")
	private String grouptype;
	
	@Mapping("permissions")  // reason for put mapping annotation here is permissions has private set.
	@OneToMany(orphanRemoval=true, cascade = CascadeType.ALL)
	private Set<Permission> permissions;	

	@Mapping("orgCanJoins")  // reason for put mapping annotation here is orgCanJoins has private set.
	@OneToMany(orphanRemoval=true, cascade = CascadeType.ALL)
	private Set<OrgCanJoin> orgCanJoins;	
	
	public Accountgroup() {
		super();
	}

	public Accountgroup(Long id, String uuid, String groupname, String accesslevel,
			String description, Date createdate, Date activatedate,
			Date suspenddate, Long organization_id, Long creator_id, String grouptype,
			Set<Permission> permissions) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.groupname = groupname;
		this.accesslevel = accesslevel;
		this.description = description;
		this.createdate = createdate;
		this.activatedate = activatedate;
		this.suspenddate = suspenddate;
		this.organization_id = organization_id;
		this.creator_id = creator_id;
		this.grouptype = grouptype;
		this.permissions = permissions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getAccesslevel() {
		return accesslevel;
	}

	public void setAccesslevel(String accesslevel) {
		this.accesslevel = accesslevel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getGrouptype() {
		return grouptype;
	}

	public void setGrouptype(String grouptype) {
		this.grouptype = grouptype;
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
	
	public void removePermission(Long permissionId){
		if(this.permissions!=null && this.permissions.size()>0){
			Permission delPermission = null;
			for(Permission p : this.permissions){
				if(p.getId().longValue()==permissionId.longValue()){
					delPermission = p;
					break;
				}
			}
			if(delPermission!=null){
				this.permissions.remove(delPermission);
			}
		}
	}
	
	public void removeAllPermissions(){
		if(this.permissions!=null && this.permissions.size()>0){
			this.permissions.clear();
		}
	}
	
	public Set<OrgCanJoin> getOrgCanJoins() {
		return orgCanJoins;
	}

	private void setOrgCanJoins(Set<OrgCanJoin> orgCanJoins) {
		this.orgCanJoins = orgCanJoins;
	}
	
	public void addOrgCanJoin(OrgCanJoin ocj){
		if(this.orgCanJoins==null) this.orgCanJoins = new HashSet<OrgCanJoin>();
		this.orgCanJoins.add(ocj);
	}
	
	public void removeAllOrgCanJoin(){
		if(this.orgCanJoins!=null && this.orgCanJoins.size()>0)
			this.orgCanJoins.clear();
	}

	public boolean isGlobalGroup(){
		if(StringUtils.isNotBlank(this.accesslevel)){
			return this.accesslevel.equals(GroupAccessLevel.Global.getCode());
		}
		return false;
	}
	
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("name",groupname).toString();
    }

	@Override
	public int compareTo(Accountgroup that) {
		
		if(this.organization_id.intValue()==that.organization_id.intValue()){
			return this.groupname.compareToIgnoreCase(that.groupname);
		}else{
			return this.organization_id.compareTo(that.organization_id);
		}
		
	}

}

package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.*;

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
@Table(name="permission")
public class Permission implements PojoInterface, Comparable<Permission>, Serializable{
	
	/**
	 * @author Stone
	 * <p>
	 * "level" here is for group or account use, some group or account only can set low level permission, 
	 * like everyone group can only setup very low lever permission type like "preview" and "read" and "copy". <br/>
	 * 
	 * The way that higher level permission merges with lower level permission still follow strict pattern:<br/>
	 * For example: <br/>
	 * 1. higher level permission is allow or deny, but lower level permission is null, the result permission is allow or deny.<br/>
	 * 2. higher level permission is allow, but lower level permission is deny, the result permission is deny.<br/>
	 * (entity can't read, it can't modify also, even entity's permission set to allow.)<br/>
	 * 3. higher level permission is deny, but lower level permission is allow, then the higher level permission is still deny, lower level permission is still allow.<br/>
	 * (entity cannot modify, but can read)<br/>
	 * </p>
	 */
	public static enum Type {
		preview(0, "The values for preview could be 'allow', 'deny' or 'null'. This is the lowest level permission, which give user or group the permission to read insensitive information."),
		read(1, "The values for read could be 'allow', 'deny' or 'null'. This permission can allow group or user to read more sensitive information for entity, like jsp, css, etc"),
		copy(3, "The values for copy could be 'allow', 'deny' or 'null'. This permission can allow group or user to copy entity(s) to user organization's tree in proper section."),
		modify(3, "The values for modify could be 'allow', 'deny' or 'null'. This permission can allow group or user to make create, update, delete for folder (category), or update, delete for leaf."),
		;
		
		private int level;
		private String desc;
		private Type(int level, String desc) {
			this.level = level;
			this.desc = desc;
		}
		
		public static Type getTypeByName(String name){
			if(StringUtils.isNotBlank(name)){
				for(Type t : Type.values()){
					if(t.name().equals(name)) return t;
				}
			}
			return null;
		}
		
		public static List<Type> getTypesForLevel(int level){
			if(level>=0){
				List<Type> results = new ArrayList<Permission.Type>();
				for(Type t : Type.values()){
					if(t.getLevel()==level){
						results.add(t);
					}
				}
				if(results.size()>0) return results;
			}
			
			return null;
		}

		public int getLevel() {
			return level;
		}

		public String getDesc() {
			return desc;
		}

		
	}

	private static final long serialVersionUID = -3084886039623121902L;
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="uuid")
	private String uuid;

	@Column(name="name")
	private String name;
	
	@Column(name="account_id")
	private Long account_id;
	
	@Column(name="group_id")
	private Long group_id;
	
	@Column(name="startdate")
	private Date startdate;
    
	@Column(name="enddate")
	private Date enddate;
	
	@Column(name="targetorg")
	private Long targetorg;
    
	@Column(name="createdate")
	private Date createdate;
	
	@Mapping("permissionedStuffs") // reason for put mapping annotation here is permissionedStuffs has private set.
	@OneToMany(orphanRemoval=true, cascade = CascadeType.ALL)
	private Set<PermissionedStuff> permissionedStuffs;

	public Permission() {
		super();
	}

	public Permission(Long id, String uuid, String name, Long account_id, Long group_id, Date startdate, Date enddate, Long targetorg, Date createdate) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.name = name;
		this.account_id = account_id;
		this.group_id = group_id;
		this.startdate = startdate;
		this.enddate = enddate;
		this.targetorg = targetorg;
		this.createdate = createdate;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public Long getTargetorg() {
		return targetorg;
	}

	public void setTargetorg(Long targetorg) {
		this.targetorg = targetorg;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Set<PermissionedStuff> getPermissionedStuffs() {
		return permissionedStuffs;
	}
	
	public List<PermissionedStuff> getPermissionedStuffsForPoint(String pointUuid){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0 && StringUtils.isNotBlank(pointUuid)){
			List<PermissionedStuff> results = new ArrayList<PermissionedStuff>();
			for(PermissionedStuff s : this.permissionedStuffs){
				if(s.getPointuuid().equals(pointUuid)){
					results.add(s);
				}
			}
			return results;
		}
		
		return null;
	}
	
	public List<PermissionedStuff> getPermissionedStuffsForType(Type type){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0 && type!=null){
			List<PermissionedStuff> results = new ArrayList<PermissionedStuff>();
			for(PermissionedStuff s : this.permissionedStuffs){
				if(s.getPermissiontype().equals(type.name())){
					results.add(s);
				}
			}
			return results;
		}
		
		return null;
	}

	public List<PermissionedStuff> getPermissionedStuffsForCategory(PermissionedStuff.Category category){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0 && category!=null){
			List<PermissionedStuff> results = new ArrayList<PermissionedStuff>();
			for(PermissionedStuff s : this.permissionedStuffs){
				if(s.getCategory().equals(category.name())){
					results.add(s);
				}
			}
			return results;
		}
		
		return null;
	}

	public List<PermissionedStuff> getPermissionedStuffsForTypeAndCategory(Type type, PermissionedStuff.Category category){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0 && category!=null && type!=null){
			List<PermissionedStuff> results = new ArrayList<PermissionedStuff>();
			for(PermissionedStuff s : this.permissionedStuffs){
				if(s.getCategory().equals(category.name()) && s.getPermissiontype().equals(type.name())){
					results.add(s);
				}
			}
			return results;
		}
		
		return null;
	}

	public PermissionedStuff getPermissionedStuffForPointWithType(String pointUuid, Permission.Type type){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0 && StringUtils.isNotBlank(pointUuid) && type!=null){
			for(PermissionedStuff s : this.permissionedStuffs){
				if(s.getPointuuid().equals(pointUuid) && s.getPermissiontype().equals(type.name())){
					return s;
				}
			}
		}
		
		return null;
	}
	
	public PermissionedStuff getPermissionedStuffByStuffid(Long stuffId){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0){
			for(PermissionedStuff s : this.permissionedStuffs){
				if(s.getId().intValue()==stuffId.intValue()){
					return s;
				}
			}
		}
		
		return null;
	}

	private void setPermissionedStuffs(Set<PermissionedStuff> permissionedStuffs) {
		this.permissionedStuffs = permissionedStuffs;
	}
	
	public void addPermissionedStuff(PermissionedStuff stuff){
		if(this.permissionedStuffs==null) this.permissionedStuffs = new HashSet<PermissionedStuff>();
		permissionedStuffs.add(stuff);
	}
	
	public void removePermissionedStuff(Long stuffId){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0){
			PermissionedStuff delStuff = null;
			for(PermissionedStuff s : this.permissionedStuffs){
				if(s.getId().intValue()==stuffId.intValue()){
					delStuff = s;
					break;
				}
			}
			if(delStuff!=null){
				this.permissionedStuffs.remove(delStuff);
			}
		}
	}
	
	public void removeAllPermissionedStuff(){
		if(this.permissionedStuffs!=null && this.permissionedStuffs.size()>0){
			this.permissionedStuffs.clear();
		}
	}
	
	/**
	 * # if startdate is null and enddate is null too, this means the schedule doesn't setup proper yet!!! <br/>
	 * # if startdate is null but enddate isn't null, this means "from now to enddate".<br/>
	 * # if startdate isn't null but enddate is null, this means "from startdate to forever"<br/>
	 * 
	 * @return compare to current date to determine if permission is using
	 */
	public boolean isPermissionInUsing(){
		Date now = new Date();
		if(this.startdate==null && this.enddate==null) return false;
		if(this.startdate!=null && this.startdate.after(now)) return false; // not begin yet!
		if(this.enddate!=null && this.enddate.before(now)) return false; // time passed yet!

		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Permission [id=").append(id).append(", name=")
				.append(name).append(", account_id=").append(account_id)
				.append(", group_id=").append(group_id)
				.append(", startdate=").append(startdate).append(", enddate=")
				.append(enddate).append(", createdate=").append(createdate)
				.append(", permissionedStuffs=").append(permissionedStuffs)
				.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(Permission that) {
		if(StringUtils.isNotBlank(name)){
			return name.compareToIgnoreCase(that.name);
		}else{
			return -1;
		}
	}

}

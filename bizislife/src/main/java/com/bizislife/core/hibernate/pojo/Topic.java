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

@Entity
@Table(name="topic")
public class Topic implements PojoInterface, Serializable{

	private static final long serialVersionUID = -5211424025673799603L;
	
	public static enum AccessLevel{
		publicTopic("1", "Public Topic"),
		privateTopic("0", "Private Topic"),
		
		;
		
		private String code;
		private String desc;
		private AccessLevel(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		public String getCode() {
			return code;
		}
		public String getDesc(){
			return desc;
		}
		public static AccessLevel fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(AccessLevel al : AccessLevel.values()){
					if(al.getCode().equals(code.trim())){
						return al;
					}
				}
			}
			return null;
		}
		@Override
		public String toString() {
			return this.getDesc();
		}
		
	}
	
	public static enum TopicType{
		SystemTopic("0"),
		personalTopic("1"),
		
		;
		private String code; // max len is 2
		private TopicType(String code){
			this.code = code;
		}
		public String getCode(){
			return code;
		}
		public static TopicType fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(TopicType t : TopicType.values()){
					if(t.getCode().equals(code.trim())){
						return t;
					}
				}
			}
			return null;
		}
	}
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="topicuuid")
	private String topicuuid;

	@Column(name="title")
	private String title;

	@Column(name="topicroute")
	private String topicroute;
	
	@Column(name="accesslevel")
	private String accesslevel;
	
	@Column(name="creator_id")
	private Long creator_id;
	
	@Column(name="org_id")
	private Long org_id;
	
	@Column(name="description")
	private String description;
	
	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="suspenddate")
	private Date suspenddate;
    
	@Column(name="topictype")
	private String topictype;
	
	@OneToMany(cascade=CascadeType.ALL)
	private Set<TopicVisibleOrg> visibleToOrgs;

	@OneToMany(cascade=CascadeType.ALL)
	private Set<TopicVisibleGroup> visibleToGroups;

	@OneToMany(cascade=CascadeType.ALL)
	private Set<TopicVisibleAccount> visibleToAccounts;

	public Topic() {
		super();
	}

	public Topic(Long id, String topicuuid, String title, String topicroute, String accesslevel,
			Long creator_id, Long org_id, String description, Date createdate,
			Date suspenddate, String topictype,
			Set<TopicVisibleOrg> visibleToOrgs,
			Set<TopicVisibleGroup> visibleToGroups,
			Set<TopicVisibleAccount> visibleToAccounts) {
		super();
		this.id = id;
		this.topicuuid = topicuuid;
		this.title = title;
		this.topicroute = topicroute;
		this.accesslevel = accesslevel;
		this.creator_id = creator_id;
		this.org_id = org_id;
		this.description = description;
		this.createdate = createdate;
		this.suspenddate = suspenddate;
		this.topictype = topictype;
		this.visibleToOrgs = visibleToOrgs;
		this.visibleToGroups = visibleToGroups;
		this.visibleToAccounts = visibleToAccounts;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTopicuuid() {
		return topicuuid;
	}

	public void setTopicuuid(String topicuuid) {
		this.topicuuid = topicuuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTopicroute() {
		return topicroute;
	}

	public void setTopicroute(String topicroute) {
		this.topicroute = topicroute;
	}

	public AccessLevel getAccesslevel() {
		return AccessLevel.fromCode(accesslevel);
	}

	public void setAccesslevel(String accesslevel) {
		this.accesslevel = accesslevel;
	}

	public Long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Long creator_id) {
		this.creator_id = creator_id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
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

	public Date getSuspenddate() {
		return suspenddate;
	}

	public void setSuspenddate(Date suspenddate) {
		this.suspenddate = suspenddate;
	}

	public TopicType getTopictype() {
		return TopicType.fromCode(topictype);
	}

	public void setTopictype(String topictype) {
		this.topictype = topictype;
	}

	public Set<TopicVisibleOrg> getVisibleToOrgs() {
		return visibleToOrgs;
	}

	private void setVisibleToOrgs(Set<TopicVisibleOrg> visibleToOrgs) {
		this.visibleToOrgs = visibleToOrgs;
	}
	
	public void addVisibleToOrg(TopicVisibleOrg visibleToOrg){
		if(this.visibleToOrgs == null) this.visibleToOrgs = new HashSet<TopicVisibleOrg>();
		this.visibleToOrgs.add(visibleToOrg);
	}
	
	public void removeAllVisibleToOrgs(){
		if(this.visibleToOrgs!=null && this.visibleToOrgs.size()>0){
			this.visibleToOrgs.clear();
		}
	}

	public Set<TopicVisibleGroup> getVisibleToGroups() {
		return visibleToGroups;
	}

	private void setVisibleToGroups(Set<TopicVisibleGroup> visibleToGroups) {
		this.visibleToGroups = visibleToGroups;
	}
	
	public void addVisibleToGroup(TopicVisibleGroup visibleToGroup){
		if(this.visibleToGroups == null) this.visibleToGroups = new HashSet<TopicVisibleGroup>();
		this.visibleToGroups.add(visibleToGroup);
	}
	
	public void removeAllVisibleToGroup(){
		if(this.visibleToGroups!=null && this.visibleToGroups.size()>0){
			this.visibleToGroups.clear();
		}
	}

	public Set<TopicVisibleAccount> getVisibleToAccounts() {
		return visibleToAccounts;
	}

	private void setVisibleToAccounts(Set<TopicVisibleAccount> visibleToAccounts) {
		this.visibleToAccounts = visibleToAccounts;
	}
	
	public void addVisibleToAccount(TopicVisibleAccount visibleToAccount){
		if(this.visibleToAccounts == null) this.visibleToAccounts = new HashSet<TopicVisibleAccount>();
		this.visibleToAccounts.add(visibleToAccount);
	}
	
	public void removeAllVisibleToAccount(){
		if(this.visibleToAccounts!=null && this.visibleToAccounts.size()>0){
			this.visibleToAccounts.clear();
		}
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("title", title).append("topicroute", topicroute).toString();
    }
	
}

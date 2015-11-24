package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="topicvisibleorg")
public class TopicVisibleOrg implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = 2289707569801945505L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="topic_id")
	private Long topic_id;
	
	@Column(name="topicuuid")
	private String topicuuid;

	@Column(name="organization_id")
	private Long organization_id;

	public TopicVisibleOrg() {
		super();
	}

	public TopicVisibleOrg(Long id, Long topic_id, String topicuuid, Long organization_id) {
		super();
		this.id = id;
		this.topic_id = topic_id;
		this.topicuuid = topicuuid;
		this.organization_id = organization_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTopic_id() {
		return topic_id;
	}

	public void setTopic_id(Long topic_id) {
		this.topic_id = topic_id;
	}

	public String getTopicuuid() {
		return topicuuid;
	}

	public void setTopicuuid(String topicuuid) {
		this.topicuuid = topicuuid;
	}

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("topic", topic_id).append("org", organization_id).toString();
    }
}

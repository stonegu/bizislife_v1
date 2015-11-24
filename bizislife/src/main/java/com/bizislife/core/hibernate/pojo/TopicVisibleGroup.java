package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="topicvisiblegroup")
public class TopicVisibleGroup implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = -1692605316560396170L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="topic_id")
	private Long topic_id;
	
	@Column(name="topicuuid")
	private String topicuuid;

	@Column(name="group_id")
	private Long group_id;

	public TopicVisibleGroup() {
		super();
	}

	public TopicVisibleGroup(Long id, Long topic_id, String topicuuid, Long group_id) {
		super();
		this.id = id;
		this.topic_id = topic_id;
		this.topicuuid = topicuuid;
		this.group_id = group_id;
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

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("topic", topic_id).append("group", group_id).toString();
    }
}

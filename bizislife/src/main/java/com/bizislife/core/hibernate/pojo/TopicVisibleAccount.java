package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="topicvisibleaccount")
public class TopicVisibleAccount implements PojoInterface, Serializable{

	private static final long serialVersionUID = -1431810059235750986L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="topic_id")
	private Long topic_id;
	
	@Column(name="topicuuid")
	private String topicuuid;

	@Column(name="account_id")
	private Long account_id;
	
	public TopicVisibleAccount() {
		super();
	}

	public TopicVisibleAccount(Long id, Long topic_id, String topicuuid, Long account_id) {
		super();
		this.id = id;
		this.topic_id = topic_id;
		this.topicuuid = topicuuid;
		this.account_id = account_id;
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

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("topic", topic_id).append("account", account_id).toString();
    }

}

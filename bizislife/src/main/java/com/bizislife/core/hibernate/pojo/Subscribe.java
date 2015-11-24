package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="subscribe")
public class Subscribe implements PojoInterface, Serializable{
	
	public static enum type{
		subscribe,
		unsubscribe,
		;
	}
	
	
	private static final long serialVersionUID = 4892833614915442689L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="topicuuid")
	private String topicuuid;

	@Column(name="account_id")
	private Long account_id;
	
	public Subscribe() {
		super();
	}

	public Subscribe(Long id, String topicuuid, Long account_id) {
		super();
		this.id = id;
		this.topicuuid = topicuuid;
		this.account_id = account_id;
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

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("topicuuid", topicuuid).append("account", account_id).toString();
    }

}

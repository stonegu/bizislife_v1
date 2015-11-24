package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="msgbody")
public class Msgbody implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = 8477170270885179684L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="body")
	private String body;

	@Column(name="createdate")
	private Date createdate;

	public Msgbody() {
		super();
	}

	public Msgbody(Long id, String body, Date createdate) {
		super();
		this.id = id;
		this.body = body;
		this.createdate = createdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("body", body).toString();
    }
}

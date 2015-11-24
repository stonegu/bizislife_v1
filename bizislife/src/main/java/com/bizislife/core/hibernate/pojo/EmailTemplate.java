package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="emailtemplate")
public class EmailTemplate implements PojoInterface, Serializable{

	private static final long serialVersionUID = -7774468105544639005L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="type")
	private String type;

	@Column(name="title")
	private String title;

	@Column(name="content")
	private String content;
	
	@Column(name="args")
	private String args;

	private EmailTemplate() {
		super();
	}

	private EmailTemplate(Long id, String type, String title, String content, String args) {
		super();
		this.id = id;
		this.type = type;
		this.title = title;
		this.content = content;
		this.args = args;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getArgs() {
		return args;
	}

	public void setArgs(String args) {
		this.args = args;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("type", type).append("title", title).toString();
    }
}

package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="tree")
public class Tree implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = -4002644741141455317L;
	
	public static enum TreeCategory {
		Topic,
		
		;
		
		public static TreeCategory fromName(String name){
			if(StringUtils.isNotBlank(name)){
				for(TreeCategory t : TreeCategory.values()){
					if(t.name().equals(name.trim())){
						return t;
					}
				}
			}
			return null;
		}
	}
	
	public static enum TreeRoot {
		sys, // root for topic
		
		;
		
		public static TreeRoot fromName(String name){
			if(StringUtils.isNotBlank(name)){
				for(TreeRoot t : TreeRoot.values()){
					if(t.name().equals(name.trim())){
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

	@Column(name="category")
	private String category;

	@Column(name="xmldata")
	private String xmldata;

	public Tree() {
		super();
	}

	public Tree(Long id, String category, String xmldata) {
		super();
		this.id = id;
		this.category = category;
		this.xmldata = xmldata;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getXmldata() {
		return xmldata;
	}

	public void setXmldata(String xmldata) {
		this.xmldata = xmldata;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("category", category).toString();
    }
}

package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="naics")
public class Naic implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = 1348753474750621643L;

	@Id
	@Column(name="id")
	private Long id;
	
	@Column(name="naicscode")
	private String naicscode;
	
	@Column(name="title")
	private String title;
	
	@Column(name="catalevel")
	private Integer catalevel;
	
	@Column(name="supercata")
	private String supercata;
	
	public Naic() {
		super();
	}

	public Naic(Long id, String naicscode, String title, Integer catalevel, String supercata) {
		super();
		this.id = id;
		this.naicscode = naicscode;
		this.title = title;
		this.catalevel = catalevel;
		this.supercata = supercata;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNaicscode() {
		return naicscode;
	}

	public void setNaicscode(String naicscode) {
		this.naicscode = naicscode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getCatalevel() {
		return catalevel;
	}

	public void setCatalevel(Integer catalevel) {
		this.catalevel = catalevel;
	}

	public String getSupercata() {
		return supercata;
	}

	public void setSupercata(String supercata) {
		this.supercata = supercata;
	}
	
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("title", title).append("level", catalevel).toString();
    }
}

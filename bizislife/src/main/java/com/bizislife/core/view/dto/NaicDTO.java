package com.bizislife.core.view.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

public class NaicDTO  implements Comparable<NaicDTO>, Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	private String naicscode;
	
	private String title;
	
	private Integer catalevel;
	
	private String supercata;
	
	private List<NaicDTO> subNaics;

	public NaicDTO() {
		super();
	}

	public Long getId() {
		return id;
	}

	public NaicDTO(Long id, String naicscode, String title, Integer catalevel,
			String supercata, List<NaicDTO> subNaics) {
		super();
		this.id = id;
		this.naicscode = naicscode;
		this.title = title;
		this.catalevel = catalevel;
		this.supercata = supercata;
		this.subNaics = subNaics;
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

	public List<NaicDTO> getSubNaics() {
		return subNaics;
	}

	public void setSubNaics(List<NaicDTO> subNaics) {
		this.subNaics = subNaics;
	}

	@Override
	public int compareTo(NaicDTO that) {
		
		return title.compareTo(that.title);
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("title", title).append("level", catalevel).toString();
    }
}

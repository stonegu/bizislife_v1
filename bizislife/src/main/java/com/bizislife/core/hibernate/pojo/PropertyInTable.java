package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(name="propertyintable")
public class PropertyInTable implements Serializable{

	private static final long serialVersionUID = -1178109006371162342L;

	public static enum PropertyType{
		// type name must <= 20 characters
		permissionMsg,
		systemFormMsg,
		systemSessionMsg,
		validateMsg,
		
		;
	}
	
	@Id
	@Column(name="pkey")
	private String pkey;
	
	@Column(name="ptype")
	private String ptype;
	
	
	@Column(name="pvalue")
	private String pvalue;
	
	@Column(name="pinfo1")
	private String pinfo1;
	
	@Column(name="pinfo2")
	private String pinfo2;
	
	public PropertyInTable() {
		super();
	}

	public PropertyInTable(String pkey, String ptype, String pvalue,
			String pinfo1, String pinfo2) {
		super();
		this.pkey = pkey;
		this.ptype = ptype;
		this.pvalue = pvalue;
		this.pinfo1 = pinfo1;
		this.pinfo2 = pinfo2;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getPkey() {
		return pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public String getPvalue() {
		return pvalue;
	}

	public void setPvalue(String pvalue) {
		this.pvalue = pvalue;
	}

	public String getPinfo1() {
		return pinfo1;
	}

	public void setPinfo1(String pinfo1) {
		this.pinfo1 = pinfo1;
	}

	public String getPinfo2() {
		return pinfo2;
	}

	public void setPinfo2(String pinfo2) {
		this.pinfo2 = pinfo2;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("type", ptype).append("key", pkey).toString();
    }
	
	
}

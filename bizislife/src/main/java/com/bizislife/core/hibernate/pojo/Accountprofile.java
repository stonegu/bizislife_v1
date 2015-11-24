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
@Table(name="accountprofile")
public class Accountprofile implements PojoInterface, Serializable{

	private static final long serialVersionUID = 536348074545356940L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="account_id")
	private Long account_id;
	
	@Column(name="firstname")
	private String firstname;
	
	@Column(name="middlename")
	private String middlename;
	
	@Column(name="lastname")
	private String lastname;
	
	@Column(name="gender")
	private String gender;
	
	@Column(name="birth_year")
	private String birth_year;
	
	@Column(name="birth_month")
	private String birth_month;
	
	@Column(name="birth_date")
	private String birth_date;
	
	@Column(name="position")
	private String position;
	
	@Column(name="timezone")
	private String timezone;

	@Column(name="createdate")
	private Date createdate;
	
	public Accountprofile() {
		super();
	}

	public Accountprofile(Long id, Long account_id, String firstname,
			String middlename, String lastname, String gender,
			String birth_year, String birth_month, String birth_date,
			String position, Date createdate) {
		super();
		this.id = id;
		this.account_id = account_id;
		this.firstname = firstname;
		this.middlename = middlename;
		this.lastname = lastname;
		this.gender = gender;
		this.birth_year = birth_year;
		this.birth_month = birth_month;
		this.birth_date = birth_date;
		this.position = position;
		this.createdate = createdate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirth_year() {
		return birth_year;
	}

	public void setBirth_year(String birth_year) {
		this.birth_year = birth_year;
	}

	public String getBirth_month() {
		return birth_month;
	}

	public void setBirth_month(String birth_month) {
		this.birth_month = birth_month;
	}

	public String getBirth_date() {
		return birth_date;
	}

	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("account id", account_id).toString();
    }
}

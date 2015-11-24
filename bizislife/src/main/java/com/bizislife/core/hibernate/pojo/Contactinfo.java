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
@Table(name="contactinfo")
public class Contactinfo implements PojoInterface, Serializable{

	private static final long serialVersionUID = 706991636446380919L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="account_id")
	private Long account_id;
	
	@Column(name="organization_id")
	private Long organization_id;
	
	@Column(name="contactnickname")
	private String contactnickname;
	
	@Column(name="email")
	private String email;
	
	@Column(name="apt_unit_number")
	private String apt_unit_number;
	
	@Column(name="street_number")
	private String street_number;
	
	@Column(name="address")
	private String address;
	
	@Column(name="city")
	private String city;
	
	@Column(name="state")
	private String state;
	
	@Column(name="zip")
	private String zip;
	
	@Column(name="country")
	private String country;
	
	@Column(name="latitude")
	private Double latitude;
	
	@Column(name="longitude")
	private Double longitude;
	
	@Column(name="dayphone_country")
	private String dayphone_country;
	
	@Column(name="dayphone_area")
	private String dayphone_area;
	
	@Column(name="dayphone")
	private String dayphone;
	
	@Column(name="dayphoneext")
	private String dayphoneext;
	
	@Column(name="eveningphone_country")
	private String eveningphone_country;
	
	@Column(name="eveningphone_area")
	private String eveningphone_area;
	
	@Column(name="eveningphone")
	private String eveningphone;
	
	@Column(name="eveningphoneext")
	private String eveningphoneext;
	
	@Column(name="mobilephone_country")
	private String mobilephone_country;
	
	@Column(name="mobilephone_area")
	private String mobilephone_area;
	
	@Column(name="mobilephone")
	private String mobilephone;
	
	@Column(name="fax")
	private String fax;
	
	@Column(name="createdate")
	private Date createdate;

	public Contactinfo() {
		super();
	}

	public Contactinfo(Long id, Long account_id, Long organization_id,
			String contactnickname, String email, String apt_unit_number,
			String street_number, String address, String city, String state,
			String zip, String country, Double latitude, Double longitude,
			String dayphone_country, String dayphone_area, String dayphone,
			String dayphoneext, String eveningphone_country,
			String eveningphone_area, String eveningphone,
			String eveningphoneext, String mobilephone_country,
			String mobilephone_area, String mobilephone, String fax,
			Date createdate) {
		super();
		this.id = id;
		this.account_id = account_id;
		this.organization_id = organization_id;
		this.contactnickname = contactnickname;
		this.email = email;
		this.apt_unit_number = apt_unit_number;
		this.street_number = street_number;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
		this.dayphone_country = dayphone_country;
		this.dayphone_area = dayphone_area;
		this.dayphone = dayphone;
		this.dayphoneext = dayphoneext;
		this.eveningphone_country = eveningphone_country;
		this.eveningphone_area = eveningphone_area;
		this.eveningphone = eveningphone;
		this.eveningphoneext = eveningphoneext;
		this.mobilephone_country = mobilephone_country;
		this.mobilephone_area = mobilephone_area;
		this.mobilephone = mobilephone;
		this.fax = fax;
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

	public Long getOrganization_id() {
		return organization_id;
	}

	public void setOrganization_id(Long organization_id) {
		this.organization_id = organization_id;
	}

	public String getContactnickname() {
		return contactnickname;
	}

	public void setContactnickname(String contactnickname) {
		this.contactnickname = contactnickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getApt_unit_number() {
		return apt_unit_number;
	}

	public void setApt_unit_number(String apt_unit_number) {
		this.apt_unit_number = apt_unit_number;
	}

	public String getStreet_number() {
		return street_number;
	}

	public void setStreet_number(String street_number) {
		this.street_number = street_number;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getDayphone_country() {
		return dayphone_country;
	}

	public void setDayphone_country(String dayphone_country) {
		this.dayphone_country = dayphone_country;
	}

	public String getDayphone_area() {
		return dayphone_area;
	}

	public void setDayphone_area(String dayphone_area) {
		this.dayphone_area = dayphone_area;
	}

	public String getDayphone() {
		return dayphone;
	}

	public void setDayphone(String dayphone) {
		this.dayphone = dayphone;
	}

	public String getDayphoneext() {
		return dayphoneext;
	}

	public void setDayphoneext(String dayphoneext) {
		this.dayphoneext = dayphoneext;
	}

	public String getEveningphone_country() {
		return eveningphone_country;
	}

	public void setEveningphone_country(String eveningphone_country) {
		this.eveningphone_country = eveningphone_country;
	}

	public String getEveningphone_area() {
		return eveningphone_area;
	}

	public void setEveningphone_area(String eveningphone_area) {
		this.eveningphone_area = eveningphone_area;
	}

	public String getEveningphone() {
		return eveningphone;
	}

	public void setEveningphone(String eveningphone) {
		this.eveningphone = eveningphone;
	}

	public String getEveningphoneext() {
		return eveningphoneext;
	}

	public void setEveningphoneext(String eveningphoneext) {
		this.eveningphoneext = eveningphoneext;
	}

	public String getMobilephone_country() {
		return mobilephone_country;
	}

	public void setMobilephone_country(String mobilephone_country) {
		this.mobilephone_country = mobilephone_country;
	}

	public String getMobilephone_area() {
		return mobilephone_area;
	}

	public void setMobilephone_area(String mobilephone_area) {
		this.mobilephone_area = mobilephone_area;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("name",contactnickname).toString();
    }
	
}

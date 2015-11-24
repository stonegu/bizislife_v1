package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="paymenthistory")
public class PaymentHistory implements PojoInterface, Serializable{

	private static final long serialVersionUID = 2468336071643819996L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="planid")
	private Long planid;
	
	@Column(name="orgid")
	private Long orgid;
	
	@Column(name="fromdate")
	private Date fromdate;

	@Column(name="todate")
	private Date todate;

	@Column(name="charged")
	private BigDecimal charged;

	public PaymentHistory() {
		super();
	}

	public PaymentHistory(Long id, Long planid, Long orgid, Date fromdate,
			Date todate, BigDecimal charged) {
		super();
		this.id = id;
		this.planid = planid;
		this.orgid = orgid;
		this.fromdate = fromdate;
		this.todate = todate;
		this.charged = charged;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPlanid() {
		return planid;
	}

	public void setPlanid(Long planid) {
		this.planid = planid;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public Date getFromdate() {
		return fromdate;
	}

	public void setFromdate(Date fromdate) {
		this.fromdate = fromdate;
	}

	public Date getTodate() {
		return todate;
	}

	public void setTodate(Date todate) {
		this.todate = todate;
	}

	public BigDecimal getCharged() {
		return charged;
	}

	public void setCharged(BigDecimal charged) {
		this.charged = charged;
	}

	@Override
	public String toString() {
		return "PaymentHistory [id=" + id + ", planid=" + planid + ", orgid="
				+ orgid + ", fromdate=" + fromdate + ", todate=" + todate
				+ ", charged=" + charged + "]";
	}
	
}

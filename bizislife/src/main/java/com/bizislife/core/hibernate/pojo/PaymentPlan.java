package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="paymentplan")
public class PaymentPlan implements PojoInterface, Serializable{
	
	private static final long serialVersionUID = 1761384843947562925L;

	public static enum Status {
		suspend("suspend"),
		activate("activate"),
		hideAndActivate("hideActi"),
		;
		
		private String code;

		private Status(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

	}
	
	public static enum defaultPlan {
		yes("1"),
		no("0"),
		;
		private String code;

		private defaultPlan(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
	}
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="paymentname")
	private String paymentname;

	@Column(name="dailypayment")
	private BigDecimal dailypayment;
	
	@Column(name="monthlypayment")
	private BigDecimal monthlypayment;
	
	@Column(name="maxinstance")
	private Long maxinstance;
	
	@Column(name="maxcharspermoduledetail")
	private Long maxcharspermoduledetail;
	
	@Column(name="maxcharsperinstance")
	private Long maxcharsperinstance;
	
	@Column(name="maxcharsperjsp")
	private Long maxcharsperjsp;
	
	@Column(name="maxcharspercss")
	private Long maxcharspercss;
	
	@Column(name="status")
	private String status;

	@Column(name="defaultplan")
	private String defaultplan;

	public PaymentPlan() {
		super();
	}

	public PaymentPlan(Long id, String paymentname, BigDecimal dailypayment,
			BigDecimal monthlypayment, Long maxinstance,
			Long maxcharspermoduledetail, Long maxcharsperinstance,
			Long maxcharsperjsp, Long maxcharspercss, String status, String defaultplan) {
		super();
		this.id = id;
		this.paymentname = paymentname;
		this.dailypayment = dailypayment;
		this.monthlypayment = monthlypayment;
		this.maxinstance = maxinstance;
		this.maxcharspermoduledetail = maxcharspermoduledetail;
		this.maxcharsperinstance = maxcharsperinstance;
		this.maxcharsperjsp = maxcharsperjsp;
		this.maxcharspercss = maxcharspercss;
		this.status = status;
		this.defaultplan = defaultplan;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentname() {
		return paymentname;
	}

	public void setPaymentname(String paymentname) {
		this.paymentname = paymentname;
	}

	public BigDecimal getDailypayment() {
		return dailypayment;
	}

	public void setDailypayment(BigDecimal dailypayment) {
		this.dailypayment = dailypayment;
	}

	public BigDecimal getMonthlypayment() {
		return monthlypayment;
	}

	public void setMonthlypayment(BigDecimal monthlypayment) {
		this.monthlypayment = monthlypayment;
	}

	public Long getMaxinstance() {
		return maxinstance;
	}

	public void setMaxinstance(Long maxinstance) {
		this.maxinstance = maxinstance;
	}

	public Long getMaxcharspermoduledetail() {
		return maxcharspermoduledetail;
	}

	public void setMaxcharspermoduledetail(Long maxcharspermoduledetail) {
		this.maxcharspermoduledetail = maxcharspermoduledetail;
	}

	public Long getMaxcharsperinstance() {
		return maxcharsperinstance;
	}

	public void setMaxcharsperinstance(Long maxcharsperinstance) {
		this.maxcharsperinstance = maxcharsperinstance;
	}

	public Long getMaxcharsperjsp() {
		return maxcharsperjsp;
	}

	public void setMaxcharsperjsp(Long maxcharsperjsp) {
		this.maxcharsperjsp = maxcharsperjsp;
	}

	public Long getMaxcharspercss() {
		return maxcharspercss;
	}

	public void setMaxcharspercss(Long maxcharspercss) {
		this.maxcharspercss = maxcharspercss;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDefaultplan() {
		return defaultplan;
	}

	public void setDefaultplan(String defaultplan) {
		this.defaultplan = defaultplan;
	}

	@Override
	public String toString() {
		return "PaymentPlan [id=" + id + ", paymentname=" + paymentname
				+ ", dailypayment=" + dailypayment + ", monthlypayment="
				+ monthlypayment + ", maxinstance=" + maxinstance
				+ ", maxcharspermoduledetail=" + maxcharspermoduledetail
				+ ", maxcharsperinstance=" + maxcharsperinstance
				+ ", maxcharsperjsp=" + maxcharsperjsp + ", maxcharspercss="
				+ maxcharspercss + ", status=" + status + "]";
	}
	

}

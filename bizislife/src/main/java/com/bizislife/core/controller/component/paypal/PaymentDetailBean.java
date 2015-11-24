package com.bizislife.core.controller.component.paypal;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@XStreamAlias("payDetail")
@XStreamConverter(PaymentDetailBean.PaymentDetailBeanConverter.class)
public class PaymentDetailBean implements Serializable{

	private static final long serialVersionUID = -3586826560960309475L;
	private String orgUuid;
	private String sellerAccountName;
	private String sellerAccountPwd;
	private String sellerSignature;
	private String sellerAppid;
	
	private String currencyCode;
	private String paymentType;
	private String shippingTotal;
	private String insuranceTotal;
	private String handlingTotal;
	private String taxTotal;
	
	private String orderDescription;
	private String notifyURL;
	private String billingAgreementText;
	private String billingType;
	
	private String returnUrl;
	private String cancelUrl;
	private String errorUrl;
	
	public String getOrgUuid() {
		return orgUuid;
	}
	public void setOrgUuid(String orgUuid) {
		this.orgUuid = orgUuid;
	}
	public String getSellerAccountName() {
		return sellerAccountName;
	}
	public void setSellerAccountName(String sellerAccountName) {
		this.sellerAccountName = sellerAccountName;
	}
	public String getSellerAccountPwd() {
		return sellerAccountPwd;
	}
	public void setSellerAccountPwd(String sellerAccountPwd) {
		this.sellerAccountPwd = sellerAccountPwd;
	}
	public String getSellerSignature() {
		return sellerSignature;
	}
	public void setSellerSignature(String sellerSignature) {
		this.sellerSignature = sellerSignature;
	}
	public String getSellerAppid() {
		return sellerAppid;
	}
	public void setSellerAppid(String sellerAppid) {
		this.sellerAppid = sellerAppid;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getShippingTotal() {
		return shippingTotal;
	}
	public void setShippingTotal(String shippingTotal) {
		this.shippingTotal = shippingTotal;
	}
	public String getInsuranceTotal() {
		return insuranceTotal;
	}
	public void setInsuranceTotal(String insuranceTotal) {
		this.insuranceTotal = insuranceTotal;
	}
	public String getHandlingTotal() {
		return handlingTotal;
	}
	public void setHandlingTotal(String handlingTotal) {
		this.handlingTotal = handlingTotal;
	}
	public String getTaxTotal() {
		return taxTotal;
	}
	public void setTaxTotal(String taxTotal) {
		this.taxTotal = taxTotal;
	}
	public String getOrderDescription() {
		return orderDescription;
	}
	public void setOrderDescription(String orderDescription) {
		this.orderDescription = orderDescription;
	}
	public String getNotifyURL() {
		return notifyURL;
	}
	public void setNotifyURL(String notifyURL) {
		this.notifyURL = notifyURL;
	}
	public String getBillingAgreementText() {
		return billingAgreementText;
	}
	public void setBillingAgreementText(String billingAgreementText) {
		this.billingAgreementText = billingAgreementText;
	}
	public String getBillingType() {
		return billingType;
	}
	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}
	public String getReturnUrl() {
		return returnUrl;
	}
	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
	public String getCancelUrl() {
		return cancelUrl;
	}
	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}
	public String getErrorUrl() {
		return errorUrl;
	}
	public void setErrorUrl(String errorUrl) {
		this.errorUrl = errorUrl;
	}
	
	
	public static class PaymentDetailBeanConverter implements Converter{

		@Override
		public boolean canConvert(Class type) {
			return type.equals(PaymentDetailBean.class);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			PaymentDetailBean paydetail = (PaymentDetailBean)source;

			writer.startNode("paydetail");
			if(StringUtils.isNotBlank(paydetail.getOrgUuid())){
				writer.startNode("orgUuid");
				writer.setValue(paydetail.getOrgUuid());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getBillingAgreementText())){
				writer.startNode("billingAgreementText");
				writer.setValue(paydetail.getBillingAgreementText());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getBillingType())){
				writer.startNode("billingType");
				writer.setValue(paydetail.getBillingType());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getCurrencyCode())){
				writer.startNode("currencyCode");
				writer.setValue(paydetail.getCurrencyCode());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getHandlingTotal())){
				writer.startNode("handlingTotal");
				writer.setValue(paydetail.getHandlingTotal());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getInsuranceTotal())){
				writer.startNode("insuranceTotal");
				writer.setValue(paydetail.getInsuranceTotal());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getNotifyURL())){
				writer.startNode("notifyURL");
				writer.setValue(paydetail.getNotifyURL());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getOrderDescription())){
				writer.startNode("orderDescription");
				writer.setValue(paydetail.getOrderDescription());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getPaymentType())){
				writer.startNode("paymentType");
				writer.setValue(paydetail.getPaymentType());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getShippingTotal())){
				writer.startNode("shippingTotal");
				writer.setValue(paydetail.getShippingTotal());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getTaxTotal())){
				writer.startNode("taxTotal");
				writer.setValue(paydetail.getTaxTotal());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getReturnUrl())){
				writer.startNode("returnUrl");
				writer.setValue(paydetail.getReturnUrl());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getCancelUrl())){
				writer.startNode("cancelUrl");
				writer.setValue(paydetail.getCancelUrl());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getErrorUrl())){
				writer.startNode("errorUrl");
				writer.setValue(paydetail.getErrorUrl());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getSellerAccountName())){
				writer.startNode("sellerAccountName");
				writer.setValue(paydetail.getSellerAccountName());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getSellerAccountPwd())){
				writer.startNode("sellerAccountPwd");
				writer.setValue(paydetail.getSellerAccountPwd());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getSellerSignature())){
				writer.startNode("sellerSignature");
				writer.setValue(paydetail.getSellerSignature());
				writer.endNode();
			}
			if(StringUtils.isNotBlank(paydetail.getSellerAppid())){
				writer.startNode("sellerAppid");
				writer.setValue(paydetail.getSellerAppid());
				writer.endNode();
			}
			writer.endNode();
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			PaymentDetailBean paydetail = null;
			if("paydetail".equals(reader.getNodeName())){
				paydetail = new PaymentDetailBean();
				while(reader.hasMoreChildren()){
					reader.moveDown();
					if("orgUuid".equals(reader.getNodeName())){
						paydetail.setOrgUuid(reader.getValue());
					}else if("currencyCode".equals(reader.getNodeName())){
						paydetail.setCurrencyCode(reader.getValue());
					}else if("paymentType".equals(reader.getNodeName())){
						paydetail.setPaymentType(reader.getValue());
					}else if("shippingTotal".equals(reader.getNodeName())){
						paydetail.setShippingTotal(reader.getValue());
					}else if("insuranceTotal".equals(reader.getNodeName())){
						paydetail.setInsuranceTotal(reader.getValue());
					}else if("handlingTotal".equals(reader.getNodeName())){
						paydetail.setHandlingTotal(reader.getValue());
					}else if("taxTotal".equals(reader.getNodeName())){
						paydetail.setTaxTotal(reader.getValue());
					}else if("orderDescription".equals(reader.getNodeName())){
						paydetail.setOrderDescription(reader.getValue());
					}else if("notifyURL".equals(reader.getNodeName())){
						paydetail.setNotifyURL(reader.getValue());
					}else if("billingAgreementText".equals(reader.getNodeName())){
						paydetail.setBillingAgreementText(reader.getValue());
					}else if("billingType".equals(reader.getNodeName())){
						paydetail.setBillingType(reader.getValue());
					}else if("returnUrl".equals(reader.getNodeName())){
						paydetail.setReturnUrl(reader.getValue());
					}else if("cancelUrl".equals(reader.getNodeName())){
						paydetail.setCancelUrl(reader.getValue());
					}else if("errorUrl".equals(reader.getNodeName())){
						paydetail.setErrorUrl(reader.getValue());
					}else if("sellerAccountName".equals(reader.getNodeName())){
						paydetail.setSellerAccountName(reader.getValue());
					}else if("sellerAccountPwd".equals(reader.getNodeName())){
						paydetail.setSellerAccountPwd(reader.getValue());
					}else if("sellerSignature".equals(reader.getNodeName())){
						paydetail.setSellerSignature(reader.getValue());
					}else if("sellerAppid".equals(reader.getNodeName())){
						paydetail.setSellerAppid(reader.getValue());
					}
					reader.moveUp();
				}
			}
			
			return paydetail;
		}
		
	}
	
}

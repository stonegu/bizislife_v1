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

@XStreamAlias("shippingAddr")
@XStreamConverter(ShippingAddressBean.ShippingAddressBeanConverter.class)
public class ShippingAddressBean implements Serializable{
	
	// Allow Address override
	public static enum AddressOverride{
		paypalShouldNotDisplayShippingAddress("0"),
		paypalShouldDisplayShippingAddress("1"),
		;
		
		private String code;

		private AddressOverride(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
		public static AddressOverride fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(AddressOverride o : AddressOverride.values()){
					if(o.getCode().equals(code)){
						return o;
					}
				}
			}
			return null;
		}
	}
	
	// Requires shipping
	public static enum NoShipping{

		DisplayShippingAddressInPayPal("0"),
		DonotDisplayShippingAddressInPayPal("1"),
		IfShippingAddressNotPassed_UseValueInBuyerProfile("2"),
		;
		
		private String code;

		private NoShipping(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
		public static NoShipping fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(NoShipping o : NoShipping.values()){
					if(o.getCode().equals(code)){
						return o;
					}
				}
			}
			return null;
		}
	}
	
	// Require buyer's PayPal Shipping address to be a confirmed address
	public static enum ReqConfirmedShipping {
		no("0"),
		yes("1"),
		;
		
		private String code;

		private ReqConfirmedShipping(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
		public static ReqConfirmedShipping fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(ReqConfirmedShipping o : ReqConfirmedShipping.values()){
					if(o.getCode().equals(code)){
						return o;
					}
				}
			}
			return null;
		}
		
	}

	private static final long serialVersionUID = 3949784958510367070L;
	private String buyerEmail;
	private String requestConfirmShipping;
	private String addressOverride;
	private String receiverName;
	private String streetAddress1;
	private String streetAddress2;
	private String city;
	private String state;
	private String postalCode;
	private String countryCode;
	private String noShipping = "0"; // default to Display shipping address in PayPal pages
	
	public String getBuyerEmail() {
		return buyerEmail;
	}
	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}
	public String getRequestConfirmShipping() {
		return requestConfirmShipping;
	}
	public void setRequestConfirmShipping(String requestConfirmShipping) {
		this.requestConfirmShipping = requestConfirmShipping;
	}
	public String getAddressOverride() {
		return addressOverride;
	}
	public void setAddressOverride(String addressOverride) {
		this.addressOverride = addressOverride;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getStreetAddress1() {
		return streetAddress1;
	}
	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}
	public String getStreetAddress2() {
		return streetAddress2;
	}
	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
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
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getNoShipping() {
		return noShipping;
	}
	public void setNoShipping(String noShipping) {
		this.noShipping = noShipping;
	}
	
	
	// convert class
	public static class ShippingAddressBeanConverter implements Converter{

		@Override
		public boolean canConvert(Class type) {
			return type.equals(ShippingAddressBean.class);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			ShippingAddressBean shippingAddr = (ShippingAddressBean)source;
			writer.startNode("shippingAddr");
			
			if(StringUtils.isNotBlank(shippingAddr.getAddressOverride())){
				writer.startNode("addressoverride");
				writer.setValue(shippingAddr.getAddressOverride());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getBuyerEmail())){
				writer.startNode("buyerMail");
				writer.setValue(shippingAddr.getBuyerEmail());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getCity())){
				writer.startNode("city");
				writer.setValue(shippingAddr.getCity());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getCountryCode())){
				writer.startNode("countryCode");
				writer.setValue(shippingAddr.getCountryCode());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getNoShipping())){
				writer.startNode("noShipping");
				writer.setValue(shippingAddr.getNoShipping());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getPostalCode())){
				writer.startNode("postalCode");
				writer.setValue(shippingAddr.getPostalCode());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getReceiverName())){
				writer.startNode("receiverName");
				writer.setValue(shippingAddr.getReceiverName());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getRequestConfirmShipping())){
				writer.startNode("reqConfirmShipping");
				writer.setValue(shippingAddr.getRequestConfirmShipping());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getState())){
				writer.startNode("state");
				writer.setValue(shippingAddr.getState());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getStreetAddress1())){
				writer.startNode("street1");
				writer.setValue(shippingAddr.getStreetAddress1());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(shippingAddr.getStreetAddress2())){
				writer.startNode("street2");
				writer.setValue(shippingAddr.getStreetAddress2());
				writer.endNode();
			}
			
			writer.endNode();
			
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			ShippingAddressBean shippingAddr = null;
			if("shippingAddr".equals(reader.getNodeName())){
				shippingAddr = new ShippingAddressBean();
				
				while(reader.hasMoreChildren()){
					reader.moveDown();
					if("buyerMail".equals(reader.getNodeName())){
						shippingAddr.setBuyerEmail(reader.getValue());
					}else if("reqConfirmShipping".equals(reader.getNodeName())){
						shippingAddr.setRequestConfirmShipping(reader.getValue());
					}else if("addressoverride".equals(reader.getNodeName())){
						shippingAddr.setAddressOverride(reader.getValue());
					}else if("receiverName".equals(reader.getNodeName())){
						shippingAddr.setReceiverName(reader.getValue());
					}else if("street1".equals(reader.getNodeName())){
						shippingAddr.setStreetAddress1(reader.getValue());
					}else if("street2".equals(reader.getNodeName())){
						shippingAddr.setStreetAddress2(reader.getValue());
					}else if("city".equals(reader.getNodeName())){
						shippingAddr.setCity(reader.getValue());
					}else if("state".equals(reader.getNodeName())){
						shippingAddr.setState(reader.getValue());
					}else if("postalCode".equals(reader.getNodeName())){
						shippingAddr.setPostalCode(reader.getValue());
					}else if("countryCode".equals(reader.getNodeName())){
						shippingAddr.setCountryCode(reader.getValue());
					}else if("noShipping".equals(reader.getNodeName())){
						shippingAddr.setNoShipping(reader.getValue());
					}
					reader.moveUp();
				}
			}
			
			return shippingAddr;
		}
		
	}
	
}

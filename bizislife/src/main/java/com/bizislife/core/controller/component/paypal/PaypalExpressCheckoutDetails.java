package com.bizislife.core.controller.component.paypal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


@XStreamAlias("paypalExpressCheckout")
@XStreamConverter(PaypalExpressCheckoutDetails.PaypalExpressCheckoutDetailsConverter.class)
public class PaypalExpressCheckoutDetails implements Serializable{

	private static final long serialVersionUID = 3270125335597767657L;
	private List<CartItemBean> items;
	private PaymentDetailBean paydetail;
	private ShippingAddressBean shippingAddr;
	public List<CartItemBean> getItems() {
		return items;
	}
	public void setItems(List<CartItemBean> items) {
		this.items = items;
	}
	
	public void addCartItem(CartItemBean item){
		if(this.items==null) this.items = new ArrayList<CartItemBean>();
		this.items.add(item);
	}
	
	public PaymentDetailBean getPaydetail() {
		return paydetail;
	}
	public void setPaydetail(PaymentDetailBean paydetail) {
		this.paydetail = paydetail;
	}
	public ShippingAddressBean getShippingAddr() {
		return shippingAddr;
	}
	public void setShippingAddr(ShippingAddressBean shippingAddr) {
		this.shippingAddr = shippingAddr;
	}
	
	public static class PaypalExpressCheckoutDetailsConverter implements Converter{

		@Override
		public boolean canConvert(Class type) {
			return type.equals(PaypalExpressCheckoutDetails.class);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			PaypalExpressCheckoutDetails details = (PaypalExpressCheckoutDetails)source;

			if(details.getItems()!=null && details.getItems().size()>0){
				writer.startNode("items");
				for(CartItemBean i : details.getItems()){
					context.convertAnother(i);
				}
				writer.endNode();
			}
			
			if(details.getPaydetail()!=null){
				context.convertAnother(details.getPaydetail());
			}
			
			if(details.getShippingAddr()!=null){
				context.convertAnother(details.getShippingAddr());
			}
			
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			PaypalExpressCheckoutDetails reqdetail = null;
			
			if("paypalExpressCheckout".equals(reader.getNodeName())){
				reqdetail = new PaypalExpressCheckoutDetails();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if("paydetail".equals(reader.getNodeName())){
						PaymentDetailBean paydetail = (PaymentDetailBean)context.convertAnother(reqdetail, PaymentDetailBean.class);
						if(paydetail!=null) reqdetail.setPaydetail(paydetail);
					}else if("shippingAddr".equals(reader.getNodeName())){
						ShippingAddressBean shippingaddr = (ShippingAddressBean)context.convertAnother(reqdetail, ShippingAddressBean.class);
						if(shippingaddr!=null) reqdetail.setShippingAddr(shippingaddr);
					}else if("items".equals(reader.getNodeName())){
						while(reader.hasMoreChildren()){
							reader.moveDown();
							if("item".equals(reader.getNodeName())){
								CartItemBean item = (CartItemBean)context.convertAnother(reqdetail, CartItemBean.class);
								if(item!=null) reqdetail.addCartItem(item);
							}
							reader.moveUp();
						}
					}
					
					reader.moveUp();
				}
				
			}
			
			return reqdetail;
		}
		
	}

}

package com.bizislife.core.controller.component.paypal;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

@XStreamAlias("item")
@XStreamConverter(CartItemBean.CartItemBeanConverter.class)
public class CartItemBean implements Serializable{
	
	private static final long serialVersionUID = -5693221584648636758L;
	private int quantity;
	private String name;
	private String amount;
	private String salesTax;
	private String category;
	private String description;
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getSalesTax() {
		return salesTax;
	}
	public void setSalesTax(String salesTax) {
		this.salesTax = salesTax;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	// xml convert class
	public static class CartItemBeanConverter implements Converter{

		@Override
		public boolean canConvert(Class type) {
			return type.equals(CartItemBean.class);
		}

		@Override
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
			CartItemBean item = (CartItemBean)source;

			writer.startNode("item");
			if(StringUtils.isNotBlank(item.getAmount())){
				writer.startNode("itemAmount");
				writer.setValue(item.getAmount());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(item.getCategory())){
				writer.startNode("itemCategory");
				writer.setValue(item.getCategory());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(item.getDescription())){
				writer.startNode("itemDescription");
				writer.setValue(item.getDescription());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(item.getName())){
				writer.startNode("itemName");
				writer.setValue(item.getName());
				writer.endNode();
			}
			
			if(StringUtils.isNotBlank(item.getSalesTax())){
				writer.startNode("salesTax");
				writer.setValue(item.getSalesTax());
				writer.endNode();
			}
			
			if(item.getQuantity()>=0){
				writer.startNode("itemQuantity");
				writer.setValue(Integer.toString(item.getQuantity()));
				writer.endNode();
			}
			writer.endNode();
		}

		@Override
		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
			CartItemBean item = null;
			
			if("item".equals(reader.getNodeName())){
				item = new CartItemBean();
				
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if("itemAmount".equals(reader.getNodeName())){
						item.setAmount(reader.getValue());
					}else if("itemCategory".equals(reader.getNodeName())){
						item.setCategory(reader.getValue());
					}else if("itemDescription".equals(reader.getNodeName())){
						item.setDescription(reader.getValue());
					}else if("itemName".equals(reader.getNodeName())){
						item.setName(reader.getValue());
					}else if("itemQuantity".equals(reader.getNodeName())){
						item.setQuantity(NumberUtils.isNumber(reader.getValue())?Integer.parseInt(reader.getValue()):0);
					}else if("salesTax".equals(reader.getNodeName())){
						item.setSalesTax(reader.getValue());
					}
					reader.moveUp();
				}
			}
			
			return item;
		}
		
	}

}

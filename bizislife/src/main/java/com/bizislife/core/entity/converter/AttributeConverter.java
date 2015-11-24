package com.bizislife.core.entity.converter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.bizislife.core.entity.AttributeAdapter;
import com.bizislife.core.entity.ImgAttribute;
import com.bizislife.core.entity.IntegerAttribute;
import com.bizislife.core.entity.RealAttribute;
import com.bizislife.core.entity.StringAttribute;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AttributeConverter implements Converter{

	@Override
	public boolean canConvert(Class type) {
		return AttributeAdapter.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		AttributeAdapter attribute = null;
		if(source.getClass().equals(RealAttribute.class)){
			attribute = (RealAttribute)source;
		}else if(source.getClass().equals(StringAttribute.class)){
			attribute = (StringAttribute)source;
		}else if(source.getClass().equals(IntegerAttribute.class)){
			attribute = (IntegerAttribute)source;
		}else if(source.getClass().equals(ImgAttribute.class)){
			attribute = (ImgAttribute)source;
		}
		writer.addAttribute("type", attribute.getClass().getSimpleName());
		writer.addAttribute("uuid", attribute.getUuid());
		writer.addAttribute("title", attribute.getTitle());
		Object value = attribute.getValue();
		writer.startNode("value");
		if(value.getClass().equals(String.class)){
			writer.setValue((String)value);
		}else if(value.getClass().equals(Double.class)){
			writer.setValue(((Double)value).toString());
		}else if(value.getClass().equals(Integer.class)){
			writer.setValue(((Integer)value).toString());
		}
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		AttributeAdapter attribute = null;
		
		String type = reader.getAttribute("type");
		if(StringUtils.isNotBlank(type)){
			if(RealAttribute.class.getSimpleName().equals(type)){
				attribute = new RealAttribute();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if("value".equals(reader.getNodeName())){
						String value = reader.getValue();
						if(NumberUtils.isNumber(value)){
							((RealAttribute)attribute).setValue(Double.valueOf(value));
						}
					}
					reader.moveUp();
				}
			}else if(IntegerAttribute.class.getSimpleName().equals(type)){
				attribute = new IntegerAttribute();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if("value".equals(reader.getNodeName())){
						String value = reader.getValue();
						if(NumberUtils.isNumber(value)){
							((IntegerAttribute)attribute).setValue(Integer.valueOf(value));
						}
					}
					reader.moveUp();
				}
			}else if(StringAttribute.class.getSimpleName().equals(type)){
				attribute = new StringAttribute();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if("value".equals(reader.getNodeName())){
						((StringAttribute)attribute).setValue(reader.getValue());
					}
					reader.moveUp();
				}
			}else if(ImgAttribute.class.getSimpleName().equals(type)){
				attribute = new ImgAttribute();
				while (reader.hasMoreChildren()) {
					reader.moveDown();
					if("value".equals(reader.getNodeName())){
						((ImgAttribute)attribute).setValue(reader.getValue());
					}
					reader.moveUp();
				}
			}
			
			if(attribute!=null){
				attribute.setUuid(reader.getAttribute("uuid"));
				attribute.setTitle(reader.getAttribute("title"));
				
				return attribute;
			}
		}
		return null;
	}

}

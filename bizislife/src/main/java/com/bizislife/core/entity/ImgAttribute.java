package com.bizislife.core.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.bizislife.core.entity.converter.AttributeConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("imgAttribute")
@XStreamConverter(AttributeConverter.class)
public class ImgAttribute extends AttributeAdapter{

	private String value;
	
	@Override
	public AttributeType getType() {
		return AttributeType.IMAGE;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if(value.getClass().equals(String.class)){
			this.value = (String)value;
		}
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("title", super.getTitle()).append("uuid", super.getUuid()).append("value", value).toString();
    }

	@Override
	public String toJson() {
		StringBuilder json = new StringBuilder();
		json.append("{'uuid':'").append(super.getUuid()).append("',").append("'title':'").append(super.getTitle()).append("',").append("'value':'").append(this.value).append("'}");
		return json.toString();
	}
}

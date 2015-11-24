package com.bizislife.core.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;

import com.bizislife.core.entity.converter.AttributeConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("intAttr")
@XStreamConverter(AttributeConverter.class)
public class IntegerAttribute extends AttributeAdapter{
	private Integer value;

	@Override
	public AttributeType getType() {
		return AttributeType.REAL;
	}

	@Override
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if(value.getClass().equals(Integer.class)){
			this.value = (Integer)value;
		}else if(value.getClass().equals(String.class) && NumberUtils.isNumber((String)value)){
			this.value = Integer.valueOf((String)value);
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

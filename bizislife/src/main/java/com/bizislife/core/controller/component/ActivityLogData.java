package com.bizislife.core.controller.component;

import java.util.*;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("ActivityLogData")
public class ActivityLogData {
	
//	@XStreamImplicit(keyFieldName="key")
	private Map<String, Object> dataMap;
	
	@XStreamAlias("desc")
	private String desc;

	public Map<String, Object> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Object> dataMap) {
		this.dataMap = dataMap;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}

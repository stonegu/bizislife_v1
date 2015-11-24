package com.bizislife.core.controller.component;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;

import org.json.simple.JSONStreamAware;
import org.json.simple.JSONValue;

public class AjaxCall implements Serializable, JSONStreamAware{

	private static final long serialVersionUID = -6131487655026572102L;

	public class Params implements Serializable, JSONStreamAware {
		private static final long serialVersionUID = -3176724118724935204L;
		
		private String targetId;
		private String type;
		public String getTargetId() {
			return targetId;
		}
		public void setTargetId(String targetId) {
			this.targetId = targetId;
		}
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		@Override
		public void writeJSONString(Writer arg0) throws IOException {
			LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
			obj.put("targetId", targetId);
			obj.put("type", type);
			JSONValue.writeJSONString(obj, arg0);
		}
	}
	
	private String url;
	private Params params;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Params getParams() {
		return params;
	}
	public void setParams(Params params) {
		this.params = params;
	}
	@Override
	public void writeJSONString(Writer arg0) throws IOException {
		LinkedHashMap<String, Object> obj = new LinkedHashMap<String, Object>();
		obj.put("url", url);
		obj.put("params", params);
		JSONValue.writeJSONString(obj, arg0);
	}
	
}

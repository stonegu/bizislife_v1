package com.bizislife.core.entity;

import java.util.*;

import com.thoughtworks.xstream.annotations.XStreamImplicit;

public abstract class EntityAdaper implements Entity{
	
	@XStreamImplicit
	List<Attribute> attrs;

	@Override
	public void addAttribute(Attribute attr) {
		if(attr!=null){
			if(attrs!=null) attrs.add(attr);
			else{
				attrs = new ArrayList<Attribute>();
				attrs.add(attr);
			}
		}
	}

	@Override
	public void removeAttribute(String attr_uuid) {
		if(attrs!=null){
			Attribute removeItem = null;
			for(Attribute a : attrs){
				if(a.getUuid().equals(attr_uuid)){
					removeItem = a;
					break;
				}
			}
			if(removeItem!=null) attrs.remove(removeItem);
		}
	}

	@Override
	public List<Attribute> getAllAttributes() {
		return attrs;
	}

}

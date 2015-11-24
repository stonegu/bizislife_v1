package com.bizislife.core.entity;

import java.util.*;

public interface Entity {
	public void addAttribute(Attribute attr);
	public void removeAttribute(String attr_uuid);
	public List<Attribute> getAllAttributes();
}

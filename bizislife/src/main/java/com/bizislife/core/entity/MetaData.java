package com.bizislife.core.entity;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * @author Stone
 *
 *	this class to hold all meta information for entity. For example, attributes list order, entity display color, etc
 *
 */
@XStreamAlias("metaData")
public class MetaData {
	private String hexcolor;

	public String getHexcolor() {
		return hexcolor;
	}

	public void setHexcolor(String hexcolor) {
		this.hexcolor = hexcolor;
	}
	

	
}

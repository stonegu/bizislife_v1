package com.bizislife.util.definition;

import org.apache.commons.lang.StringUtils;

public class EmailRelated {
	
	public static enum EmailType{
		applicationSubmit,
		bugReport,
		contactInfo,
		newAccount_activate,
		
		
		;
		
		public static EmailType fromString(String name){
			if(StringUtils.isNotBlank(name)){
				for(EmailType t : EmailType.values()){
					if(t.name().equals(name))
						return t;
				}
			}
			return null;
		}
	}

}

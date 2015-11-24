package com.bizislife.util.definition;

import org.apache.commons.lang.StringUtils;

public class DatabaseRelatedCode {
	
	
	// ******* all predefined data related to organization table
	public static enum OrganizationRelated {
		
		orglevel_bizislife("0", "bizislife level"),
		orglevel_other("1", "other organization's level"),
		
		;
		
		
		
		private OrganizationRelated(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		private String code;
		private String desc;
		
	}
	
	// ******* all predefined data related to account table
	public static enum AccountRelated {
		
		accountIdForSystem("0", "system account"),
		//accountIdForSelf("-1", "self account"), // don't need this??
		accountIdForAnnonymous("-1", "annonymous account"),
		
		permission_admin("p1", "admin account"),
		permission_normal("p0", "normal account"),
		
		gender_female("f", "female"),
		gender_male("m", "male"),
		
		
		
		;
		
		private AccountRelated(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		
		
		public String getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
		
		public static AccountRelated fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(AccountRelated a : AccountRelated.values()){
					if(a.getCode().equals(code.trim())){
						return a;
					}
				}
			}
			return null;
		}

		private String code; // code can't be duplicated!!!
		private String desc;
	}
	

}

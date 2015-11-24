package com.bizislife.util.definition;

public class AttributeList {
	
	public static enum SessionAttribute{
		back_url,
		errorMsgs,
		loginAccount,
		//orgPaymentProfile,
		;
	}
	
	public static enum RequestAttribute{
		currentAccountPageId, // used for hightlight accountNav_main1 tree
		currentAction,
		currentPageId, // used for highlight leftNav_main2 tree
		errorMsgs,
		orguuid,
		;
	}

}

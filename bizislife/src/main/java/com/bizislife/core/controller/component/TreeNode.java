package com.bizislife.core.controller.component;


import com.bizislife.core.view.dto.AccountDto;

public interface TreeNode {
	public static String SYSTEMNAME = "systemName";
	
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount);
	public String getPrettyName();
	/**
	 * @return node's systemName (uuid in most cases)
	 */
	public String getSystemName();
}

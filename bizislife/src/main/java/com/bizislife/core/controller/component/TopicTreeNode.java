package com.bizislife.core.controller.component;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.*;


import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.bizislife.core.controller.component.AjaxCall.Params;
import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.Topic;
import com.bizislife.core.view.dto.AccountDto;
import com.thoughtworks.xstream.annotations.*;

@XStreamAlias("topicNode")
public class TopicTreeNode implements TreeNode{

	@XStreamOmitField
	final String defaultIcon = "img/vendor/web-icons/document-medium.png";

	private String prettyName; // like: sys; org; create; ...
	private String systemName; // uuid
	@XStreamImplicit
	private List<TopicTreeNode> subnodes;
	// more information for privilege:
	private Privilege privilege;
	@XStreamOmitField
	private String cssClassInfos; // this cssClassInfos will be added into jstreeNode's cssClass. Example of extraInfo: "sys notDel"
	
	@XStreamOmitField
	private Map<Topic.TopicType, Integer> childrenNumbers; // hold count numbers for different children. Folder number, product number, etc.

	public String getPrettyName() {
		return prettyName;
	}

	public void setPrettyName(String prettyName) {
		this.prettyName = prettyName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public List<TopicTreeNode> getSubnodes() {
		return subnodes;
	}

	public void setSubnodes(List<TopicTreeNode> subnodes) {
		this.subnodes = subnodes;
	}

	public Privilege getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Privilege privilege) {
		this.privilege = privilege;
	}

	@JsonIgnore
	public String getCssClassInfos() {
		return cssClassInfos;
	}

	public void setCssClassInfos(String cssClassInfos) {
		this.cssClassInfos = cssClassInfos;
	}
	
	public void addCssClassInfos(String cssClasses){
		if(StringUtils.isNotBlank(this.cssClassInfos)){
			this.cssClassInfos = this.cssClassInfos + " " + cssClasses;
		}else{
			this.cssClassInfos = cssClasses;
		}
		
	}
	public Map<Topic.TopicType, Integer> getChildrenNumbers() {
		return childrenNumbers;
	}

	public void setChildrenNumbers(Map<Topic.TopicType, Integer> childrenNumbers) {
		this.childrenNumbers = childrenNumbers;
	}
	
	public void addChildrenNumber(Topic.TopicType childType, int countNumber) {
		if(this.childrenNumbers==null){
			this.childrenNumbers = new HashMap<Topic.TopicType, Integer>();
		}
		this.childrenNumbers.put(childType, Integer.valueOf(countNumber));
	}

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("prettyName",prettyName).toString();
    }

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		StringBuilder thisPath = new StringBuilder();
		if(StringUtils.isNotBlank(path)) thisPath.append(path).append(".").append(prettyName);
		else thisPath.append(prettyName);

		JsTreeNode ret = new JsTreeNode();
		
		// to add ajax call params for topic info
//		AjaxCall tooltipAjaxCall = new AjaxCall();
//		Params ajaxCallParams = tooltipAjaxCall.new Params();
//		ajaxCallParams.setTargetId(systemName);
//		ajaxCallParams.setType("info");
//		tooltipAjaxCall.setParams(ajaxCallParams);
//		tooltipAjaxCall.setUrl("getTopicInfo");
//		StringWriter jsonOut = new StringWriter();
//		try {
//			tooltipAjaxCall.writeJSONString(jsonOut);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		Data data = ret.new Data();
		
//		data.setTitle(new StringBuilder("<span class='detailInfoPopup'")
//			.append(" domvalue='{\"ajaxCall\":").append(jsonOut.toString()).append("}'")
//			.append(">").append(prettyName).append("</span>").toString());
		data.setTitle(prettyName);
		Attr dataAttr = ret.new Attr();
		dataAttr.setTitle(thisPath.toString());

		// for editable
//		if(currentAccount!=null){
//			if(this.privilege!=null && this.privilege.getOrgId()!=null && this.privilege.getOrgId().equals(currentAccount.getOrganization_id())){
//				dataAttr.addCssClass("editable info"); // class "editable" : current user can edit current node (topic)
//			}else{
//				dataAttr.addCssClass("info"); // class "info" : current user can see the node information
//			}
//		}
		
		data.setIcon(defaultIcon);
		data.setAttr(dataAttr);
		ret.setData(data);

		Attr attr = ret.new Attr();
		String nodeId = systemName!=null?systemName:UUID.randomUUID().toString();
		attr.setId(nodeId);
		//attr.setDomvalue(thisPath.toString());
		// selected or not
		if(ArrayUtils.contains(checkedNodeList, nodeId)){
			attr.addCssClass("jstree-checked");
		}
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		
		ret.setAttr(attr);
		
		if(subnodes!=null && subnodes.size()>0){
			List<JsTreeNode> tns = new ArrayList<JsTreeNode>();
			for(TopicTreeNode n : subnodes){
				tns.add(n.switchToJsTreeNode(thisPath.toString(), checkedNodeList, currentAccount));
			}
			if(tns.size()>0){
				ret.setChildren(tns);
				ret.setState("closed");
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
				attr.setFoldOpenAfterLoad(true);
			}else{
				attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());
				ret.setState("");
			}
		}else{
			
			if(childrenNumbers!=null && childrenNumbers.size()>0){
				boolean closed = false;
				for(Map.Entry<Topic.TopicType, Integer> e : childrenNumbers.entrySet()){
					if(e.getValue()>0){
						closed = true;
						break;
					}
				}
				
				if(closed) ret.setState("closed");
				else ret.setState("");
				
			}else{
				ret.setState("");
			}
			
			attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());
		}
		
		return ret;
	}

}

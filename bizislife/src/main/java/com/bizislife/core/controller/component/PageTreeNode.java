package com.bizislife.core.controller.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.view.dto.AccountDto;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("pageNode")
public class PageTreeNode implements TreeNode{
	private String prettyName;
	private String systemName;
	@XStreamImplicit
	private List<PageTreeNode> subnodes;
	@XStreamOmitField
	private PageDetail.Type type;
	@XStreamOmitField
	private Map<JsTreeNode.NodeType, Integer> childrenNumbers; // hold count numbers for different children. Folder number, mobile page number, desktop number, etc.
	@XStreamOmitField
	private String cssClassInfos; // this cssClassInfos will be added into jstreeNode's cssClass. Example of extraInfo: "sys notDel"
	
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

	public List<PageTreeNode> getSubnodes() {
		return subnodes;
	}

	public void setSubnodes(List<PageTreeNode> subnodes) {
		this.subnodes = subnodes;
	}

	public PageDetail.Type getType() {
		return type;
	}

	public void setType(PageDetail.Type type) {
		this.type = type;
	}

	public Map<JsTreeNode.NodeType, Integer> getChildrenNumbers() {
		return childrenNumbers;
	}

	public void setChildrenNumbers(Map<JsTreeNode.NodeType, Integer> childrenNumbers) {
		this.childrenNumbers = childrenNumbers;
	}

	public void addChildrenNumber(JsTreeNode.NodeType childType, int countNumber) {
		if(this.childrenNumbers==null){
			this.childrenNumbers = new HashMap<JsTreeNode.NodeType, Integer>();
		}
		this.childrenNumbers.put(childType, Integer.valueOf(countNumber));
	}

	public String getCssClassInfos() {
		return cssClassInfos;
	}

	public void setCssClassInfos(String cssClassInfos) {
		this.cssClassInfos = cssClassInfos;
	}
	
	public void addCssClass(String cssClass){
		if(StringUtils.isNotBlank(this.cssClassInfos)){
			this.cssClassInfos = this.cssClassInfos + " " + cssClass;
		}else{
			this.cssClassInfos = cssClass;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageTreeNode [prettyName=").append(prettyName)
				.append(", systemName=").append(systemName)
				.append(", subnodes=").append(subnodes).append(", type=")
				.append(type).append(", childrenNumbers=")
				.append(childrenNumbers).append("]");
		return builder.toString();
	}
	
	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(prettyName);
		Attr dataAttr = ret.new Attr();
		ret.setData(data);
		
		Attr attr = ret.new Attr();
		attr.setId(systemName!=null?systemName:UUID.randomUUID().toString());
		ret.setAttr(attr);
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		
		if(subnodes!=null && subnodes.size()>0){
			List<JsTreeNode> tns = new ArrayList<JsTreeNode>();
			for(PageTreeNode n : subnodes){
				tns.add(n.switchToJsTreeNode("", checkedNodeList, currentAccount));
			}
			if(tns.size()>0){
				ret.setChildren(tns);
				ret.setState("closed");
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
//				attr.setFoldOpenAfterLoad(true);
			}else{
				attr.setRel(JsTreeNode.NodeType.leafPage.getSystemName());
				ret.setState("");
			}
		}else{
			if(type!=null && (type.equals(PageDetail.Type.Folder))){
				//attr.setLfc("Click for more children.");
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
				if(childrenNumbers!=null && childrenNumbers.size()>0){
					boolean closed = false;
					for(Map.Entry<JsTreeNode.NodeType, Integer> e : childrenNumbers.entrySet()){
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
			}else{
				ret.setState("");
				attr.setRel(JsTreeNode.NodeType.leafPage.getSystemName());
				
				// already set cssClass in TreeService
//				if(type!=null && type.equals(PageDetail.Type.Mobile)){
//					attr.addCssClass(PageDetail.Type.Mobile.getCode());
//				}
//				if(type!=null && type.equals(PageDetail.Type.Desktop)){
//					attr.addCssClass(PageDetail.Type.Desktop.getCode());
//				}
				
			}
		}
		
		return ret;
	}


}

package com.bizislife.core.controller.component;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.view.dto.AccountDto;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "leftNavNode")
public class LeftNavNode implements Cloneable, TreeNode{
	
	@XStreamOmitField
	final String defaultIcon = "img/vendor/web-icons/document-medium.png";
	
	private String prettyName;
	private String systemName;
	private String url; // this is the url when you click the node
	private String subNodesUrl; // this is used for ajax call to get json data for subNodes
	private String icon;
	private List<LeftNavNode> subnodes;
	private boolean foldOpenAfterLoad;

	@XmlElement(name = "prettyName")
	public String getPrettyName() {
		return prettyName;
	}

	public void setPrettyName(String prettyName) {
		this.prettyName = prettyName;
	}

	@XmlElement(name = "systemName")
	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@XmlElement(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@XmlElement(name = "subNodesUrl")
	public String getSubNodesUrl() {
		return subNodesUrl;
	}

	public void setSubNodesUrl(String subNodesUrl) {
		this.subNodesUrl = subNodesUrl;
	}

	@XmlElement(name = "icon")
	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	@XmlElement(name = "subNodes")
	public List<LeftNavNode> getSubnodes() {
		return subnodes;
	}

	public void setSubnodes(List<LeftNavNode> subnodes) {
		this.subnodes = subnodes;
	}
	
	@XmlElement(name = "foldOpenAfterLoad")
	public boolean getFoldOpenAfterLoad() {
		return foldOpenAfterLoad;
	}

	public void setFoldOpenAfterLoad(boolean foldOpenAfterLoad) {
		this.foldOpenAfterLoad = foldOpenAfterLoad;
	}

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount){
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(prettyName);
		Attr dataAttr = ret.new Attr();
		dataAttr.setHref(url!=null?url:"/#");
		data.setAttr(dataAttr);
		if(StringUtils.isNotBlank(icon)){
			data.setIcon(icon);
		}else{
			data.setIcon(defaultIcon);
		}
		ret.setData(data);
		
		Attr attr = ret.new Attr();
		attr.setId(systemName!=null?systemName:UUID.randomUUID().toString());
		if(foldOpenAfterLoad) attr.setFoldOpenAfterLoad(true);
		ret.setAttr(attr);
		
		if(subnodes!=null && subnodes.size()>0){
			List<JsTreeNode> tns = new ArrayList<JsTreeNode>();
			for(LeftNavNode n : subnodes){
				tns.add(n.switchToJsTreeNode("", checkedNodeList, currentAccount));
			}
			if(tns.size()>0){
				ret.setChildren(tns);
				ret.setState("closed");
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
//				attr.setFoldOpenAfterLoad(true);
			}else{
				attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());
				ret.setState("");
			}
		}else{
			if(StringUtils.isNotBlank(subNodesUrl)){
				ret.setState("closed");
				attr.setLfc(subNodesUrl);
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
//				attr.setFoldOpenAfterLoad(true);
			}else{
				ret.setState("");
				attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());
			}
		}
		
		return ret;
	}
	
	public void attachSuffixToSystemName(String suffix){
		if(StringUtils.isNotBlank(suffix)){
			this.systemName = new StringBuilder(this.systemName).append("_").append(suffix).toString();
			if(this.subnodes!=null && this.subnodes.size()>0){
				for(LeftNavNode n : this.subnodes){
					n.attachSuffixToSystemName(suffix);
				}
			}
		}
	}
	
	public void attachParamsToUrl(String params){
		if(StringUtils.isNotBlank(params)){
			if(StringUtils.isNotBlank(this.url)) this.url = new StringBuilder(this.url).append("?").append(params.trim()).toString();
			if(this.subnodes!=null && this.subnodes.size()>0){
				for(LeftNavNode n : this.subnodes){
					n.attachParamsToUrl(params);
				}
			}
		}
	}

	public LeftNavNode clone(){
		LeftNavNode cloned = new LeftNavNode();
		cloned.setIcon(this.icon!=null?this.icon:null);
		cloned.setPrettyName(this.prettyName!=null?this.prettyName:null);
		cloned.setSubNodesUrl(this.subNodesUrl!=null?this.subNodesUrl:null);
		cloned.setSystemName(this.systemName!=null?this.systemName:null);
		cloned.setUrl(this.url!=null?this.url:null);
		if(this.subnodes!=null && this.subnodes.size()>0){
			List<LeftNavNode> subN = new ArrayList<LeftNavNode>();
			for(LeftNavNode n : this.subnodes){
				subN.add(n.clone());
			}
			if(subN.size()>0){
				cloned.subnodes = subN;
			}
		}
		return cloned;
	}
	
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("prettyName",prettyName).append("url", url).toString();
    }

}

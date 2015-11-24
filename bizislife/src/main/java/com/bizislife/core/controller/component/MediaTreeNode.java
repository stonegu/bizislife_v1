package com.bizislife.core.controller.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import scala.collection.mutable.StringBuilder;

import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.view.dto.AccountDto;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("mediaNode")
public class MediaTreeNode implements TreeNode{
	
	private String prettyName;
	private String systemName;
	@XStreamImplicit
	private List<MediaTreeNode> subnodes;
	@XStreamOmitField
	private MediaDetail.MediaType type;
	@XStreamOmitField
	private Map<JsTreeNode.NodeType, Integer> childrenNumbers; // hold count numbers for different children. Folder number, product number, etc.
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

	public List<MediaTreeNode> getSubnodes() {
		return subnodes;
	}

	public void setSubnodes(List<MediaTreeNode> subnodes) {
		this.subnodes = subnodes;
	}

	public MediaDetail.MediaType getType() {
		return type;
	}

	public void setType(MediaDetail.MediaType type) {
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
	
	public void addCssClassInfos(String cssClassInfos) {
		if(this.cssClassInfos==null || this.cssClassInfos.length()<=0) this.cssClassInfos = cssClassInfos;
		else {
			this.cssClassInfos = new StringBuilder(this.cssClassInfos).append(" ").append(cssClassInfos).toString();
		}
	}

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(prettyName);
		

		if(type!=null && type.equals(MediaDetail.MediaType.image)){
			Attr dataAttr = ret.new Attr();
			dataAttr.addCssClass("detailInfoPopup");
			
//			dataAttr.setDomvalue("{'topOffset':20, 'leftOffset':-30, 'ajaxCall':{'url':'/getphoto', 'params':'id="+systemName+"&size=20'}}");
			dataAttr.setDomvalue("{'topOffset':20, 'leftOffset':-30, 'popupContent':'<img src=/getphoto?id="+systemName+"&size=100>'}");
			
			data.setAttr(dataAttr);
			
		}

		ret.setData(data);
		
		Attr attr = ret.new Attr();
		attr.setId(systemName!=null?systemName:UUID.randomUUID().toString());
		ret.setAttr(attr);
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		
		if(subnodes!=null && subnodes.size()>0){
			List<JsTreeNode> tns = new ArrayList<JsTreeNode>();
			for(MediaTreeNode n : subnodes){
				tns.add(n.switchToJsTreeNode("", checkedNodeList, currentAccount));
			}
			if(tns.size()>0){
				ret.setChildren(tns);
				ret.setState("closed");
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
//				attr.setFoldOpenAfterLoad(true);
			}else{
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
				ret.setState("");
			}
			
		}else{
			if(type!=null && type.equals(MediaDetail.MediaType.folder)){
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
				if(type!=null && type.equals(MediaDetail.MediaType.image)){
					attr.setRel(JsTreeNode.NodeType.leafImg.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.pdf)){
					attr.setRel(JsTreeNode.NodeType.leafPdf.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.text)){
					attr.setRel(JsTreeNode.NodeType.leafText.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.css)){
					attr.setRel(JsTreeNode.NodeType.leafCss.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.javascript)){
					attr.setRel(JsTreeNode.NodeType.leafJS.getSystemName());
				}else{
					attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());
				}
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * return back a JsTreeNode where the node is not the mediatype and folder will be marked as "noSelectable"
	 * 
	 * @param mediaType
	 * @return
	 */
	public JsTreeNode switchToJsTreeNode_type(MediaDetail.MediaType mediaType) {
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
			for(MediaTreeNode n : subnodes){
				tns.add(n.switchToJsTreeNode_type(mediaType));
			}
			if(tns.size()>0){
				ret.setChildren(tns);
				ret.setState("closed");
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
//				attr.setFoldOpenAfterLoad(true);
			}else{
				attr.setRel(JsTreeNode.NodeType.folder.getSystemName());
				ret.setState("");
			}
			
		}else{
			if(type!=null && type.equals(MediaDetail.MediaType.folder)){
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
				if(type!=null && type.equals(MediaDetail.MediaType.image)){
					attr.setRel(JsTreeNode.NodeType.leafImg.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.pdf)){
					attr.setRel(JsTreeNode.NodeType.leafPdf.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.text)){
					attr.setRel(JsTreeNode.NodeType.leafText.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.css)){
					attr.setRel(JsTreeNode.NodeType.leafCss.getSystemName());
				}else if(type!=null && type.equals(MediaDetail.MediaType.javascript)){
					attr.setRel(JsTreeNode.NodeType.leafJS.getSystemName());
				}else{
					attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());
				}
				
				if(type!=mediaType)
					attr.addCssClass("noSelectable");
			}
		}
		
		return ret;
	}

}

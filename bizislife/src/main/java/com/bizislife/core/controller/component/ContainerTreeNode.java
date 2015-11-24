package com.bizislife.core.controller.component;

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.hibernate.pojo.ContainerDetail;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.view.dto.AccountDto;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("containerNode")
public class ContainerTreeNode implements TreeNode{
	private String prettyName;
	private String systemName;
	@XStreamOmitField
	private boolean isRoot;
	@XStreamImplicit
	private List<ContainerTreeNode> subnodes;
	@XStreamOmitField
	private int childrenNumbers;
	@XStreamOmitField
	private String cssClassInfos; // this cssClassInfos will be added into jstreeNode's cssClass. Example of extraInfo: "sys notDel"
	@XStreamOmitField
	private String domvalue; // hold some container detail info here, like direction, ...
	
	@XStreamOmitField
	private String direction;
	@XStreamOmitField
	private Integer topposition;
	@XStreamOmitField
	private Integer leftposition;
	@XStreamOmitField
	private Integer width;
	@XStreamOmitField
	private Double relativeWidth; // used for pageRetriver's relative width. Note: this number is already multiply 100!
	@XStreamOmitField
	private Integer height;
	@XStreamOmitField
	private Double relativeHeight; // used for pageRetriver's relative position. Note: this number is already multiply 100!
	@XStreamOmitField
	private boolean deletable;
	@XStreamOmitField
	private boolean editable;
	@XStreamOmitField
	private String hexColor;

	@XStreamOmitField
	private Integer marginTop; //used for pageRetriver's relative position
	@XStreamOmitField
	private Integer marginLeft; //used for pageRetriver's relative position
	@XStreamOmitField
	private Double relativeMarginLeft; //used for pageRetriver's relative position. Note: this number is already multiply 100!
	
	
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

	public List<ContainerTreeNode> getSubnodes() {
		return subnodes;
	}

	public void setSubnodes(List<ContainerTreeNode> subnodes) {
		this.subnodes = subnodes;
	}
	
	public void addSubnode(ContainerTreeNode subnode){
		if(subnodes==null) subnodes = new ArrayList<ContainerTreeNode>();
		subnodes.add(subnode);
	}

	public int getChildrenNumbers() {
		return childrenNumbers;
	}

	public void setChildrenNumbers(int childrenNumbers) {
		this.childrenNumbers = childrenNumbers;
	}
	
	public String getCssClassInfos() {
		return cssClassInfos;
	}

	public void setCssClassInfos(String cssClassInfos) {
		if(StringUtils.isBlank(this.cssClassInfos)){
			this.cssClassInfos = cssClassInfos;
		}else{
			this.cssClassInfos = this.cssClassInfos+" "+cssClassInfos;
		}
	}
	
	public void removeCssClassInfos(){
		this.cssClassInfos = null;
	}

	public String getDomvalue() {
		return domvalue;
	}

	public void setDomvalue(String domvalue) {
		this.domvalue = domvalue;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public Integer getTopposition() {
		return topposition;
	}

	public void setTopposition(Integer topposition) {
		this.topposition = topposition;
	}

	public Integer getLeftposition() {
		return leftposition;
	}

	public void setLeftposition(Integer leftposition) {
		this.leftposition = leftposition;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}

	public Integer getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(Integer marginTop) {
		this.marginTop = marginTop;
	}

	public Integer getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(Integer marginLeft) {
		this.marginLeft = marginLeft;
	}

	public Double getRelativeWidth() {
		return relativeWidth;
	}

	public void setRelativeWidth(Double relativeWidth) {
		this.relativeWidth = relativeWidth;
	}

	public Double getRelativeHeight() {
		return relativeHeight;
	}

	public void setRelativeHeight(Double relativeHeight) {
		this.relativeHeight = relativeHeight;
	}

	public Double getRelativeMarginLeft() {
		return relativeMarginLeft;
	}

	public void setRelativeMarginLeft(Double relativeMarginLeft) {
		this.relativeMarginLeft = relativeMarginLeft;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(prettyName);
		ret.setData(data);
		
		// for Data's attr
		Attr dataAttr = ret.new Attr();
		dataAttr.setCssClasses("hex_"+hexColor);
		data.setAttr(dataAttr);
		
		
		
		Attr attr = ret.new Attr();
		attr.setId(systemName!=null?systemName:UUID.randomUUID().toString());
		ret.setAttr(attr);
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		attr.setRel(JsTreeNode.NodeType.container.getSystemName());
		if(StringUtils.isNotBlank(domvalue)){
			attr.setDomvalue(domvalue);
		}
		
		if(subnodes!=null && subnodes.size()>0){
			List<JsTreeNode> tns = new ArrayList<JsTreeNode>();
			for(ContainerTreeNode n : subnodes){
				tns.add(n.switchToJsTreeNode("", checkedNodeList, currentAccount));
			}
			if(tns.size()>0){
				ret.setChildren(tns);
				ret.setState("closed");
//				attr.setFoldOpenAfterLoad(true);
			}else{
				ret.setState("");
			}
		}else{

			if(childrenNumbers>0){
				ret.setState("closed");
			}else{
				ret.setState("");
			}
		}
		
		return ret;
	}
	
	
	
	public static Comparator<ContainerTreeNode> positionSort = new Comparator<ContainerTreeNode>() {
	
		public int compare(ContainerTreeNode node1, ContainerTreeNode node2) {
			//a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
			if(StringUtils.equals(node1.getDirection(), node2.getDirection())){
				// left to right
				if(node1.getDirection()!=null && node1.getDirection().equals(ContainerDetail.Direction.Left2Right.getCode())){
					if(node1.getTopposition()==null){
						return -1;
					}else if(node2.getTopposition()==null){
						return 1;
					}else if(node1.getTopposition().intValue()>node2.getTopposition().intValue()){
						return 1; 
					}else if(node1.getTopposition().intValue()<node2.getTopposition().intValue()){
						return -1;
					}
				}else if(node1.getDirection()!=null && node1.getDirection().equals(ContainerDetail.Direction.Top2Bottom.getCode())){
					if(node1.getLeftposition()==null){
						return -1;
					}else if(node2.getLeftposition()==null){
						return 1;
					}else if(node1.getLeftposition().intValue()>node2.getLeftposition().intValue()){
						return 1;
					}else if(node1.getLeftposition().intValue()<node2.getLeftposition().intValue()){
						return -1;
					}
				}
			}else{
				return 0;
			}
			
			return 0;
		}
	
	};	
	
	

}

package com.bizislife.core.controller.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import scala.collection.mutable.StringBuilder;

public class JsTreeNode  implements java.io.Serializable {
	
	private static final long serialVersionUID = 822072466726807250L;

	public static enum NodeType {
		container("container"),
		containerModuleSchedule("cmSched"),
		drive("drive"),
		folder("folder"),
		instance("instance"),
		instanceView("instanceView"),
		leaf("default"),
		leafCss("css"),
		leafImg("image"),
		leafJS("javascript"),
		leafMsDoc("msdoc"),
		leafPage("page"),
		leafPdf("pdf"),
		leafText("text"),
		module("module"),
		moduleInstanceSchedule("miSched"),
		node("node"), // can be folder or leaf or drive
		org("org"),
//		product("product"),
		root("root"),
		;
		
		private String systemName;
		private NodeType(String systemName) {
			this.systemName = systemName;
		}
		public String getSystemName() {
			return systemName;
		}
		
		public static NodeType fromSysName(String sysName){
			if(StringUtils.isNotBlank(sysName)){
				for(NodeType type : NodeType.values()){
					if(type.getSystemName().equals(sysName)){
						return type;
					}
				}
			}
			return null;
		}
	}

	public class Attr implements java.io.Serializable {
		private static final long serialVersionUID = 8295206864837976177L;
		
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String id;
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String rel; // for jsTree required info : drive, folder, default
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String href; // this is the url for click the node
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String lfc; // linkForChildren : the link for Ajax call to get the children
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String title; // for tooltip
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String domvalue; // for holding some data
		@JsonProperty("class")
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String cssClasses; // class names in html
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private Boolean foldOpenAfterLoad; // true: open folder after tree loader
		
		public Attr() {
			super();
		}
		public Attr(String id, String rel, String href,String lfc, String title, String domvalue, String cssClasses, Boolean foldOpenAfterLoad) {
			super();
			this.id = id;
			this.rel = rel;
			this.href = href;
			this.lfc = lfc;
			this.title = title;
			this.domvalue = domvalue;
			this.cssClasses = cssClasses;
			this.foldOpenAfterLoad = foldOpenAfterLoad;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getRel() {
			return rel;
		}
		public void setRel(String rel) {
			this.rel = rel;
		}
		public void addRel(String rel){
			if(StringUtils.isNotBlank(this.rel)){
				this.rel = new StringBuilder(this.rel).append(" ").append(rel).toString();
			}else{
				this.rel = rel;
			}
		}
		public String getHref() {
			return href;
		}
		public void setHref(String href) {
			this.href = href;
		}
		public String getLfc() {
			return lfc;
		}
		public void setLfc(String lfc) {
			this.lfc = lfc;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getDomvalue() {
			return domvalue;
		}
		public void setDomvalue(String domvalue) {
			this.domvalue = domvalue;
		}
		public String getCssClasses() {
			return cssClasses;
		}
		public void setCssClasses(String cssClasses) {
			this.cssClasses = cssClasses;
		}
		public void addCssClass(String cssClass){
			if(this.cssClasses==null || this.cssClasses.length()<=0) this.cssClasses = cssClass;
			else {
				this.cssClasses = new StringBuilder(this.cssClasses).append(" ").append(cssClass).toString();
			}
		}
		public void removeCssClass(String cssClass){
			if(StringUtils.isNotBlank(cssClass) && StringUtils.isNotBlank(this.cssClasses)){
				String [] classes = cssClass.split(" ");
				if(classes!=null && classes.length>0){
					for(String c : classes){
						this.cssClasses.replaceAll(c.trim(), "");
					}
				}
			}
		}
		public Boolean isFoldOpenAfterLoad() {
			return foldOpenAfterLoad;
		}
		public void setFoldOpenAfterLoad(Boolean foldOpenAfterLoad) {
			this.foldOpenAfterLoad = foldOpenAfterLoad;
		}
		@Override
	    public String toString(){
	        return new ToStringBuilder(this).append("id",id).append("rel", rel).toString();
	    }
	}
	
	public class Data implements java.io.Serializable {
		private static final long serialVersionUID = 8295206864837976177L;
		
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String title;
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private Attr attr;
		@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
		private String icon;
		
		public Data() {
			super();
		}
		public Data(String title, Attr attr, String icon) {
			super();
			this.title = title;
			this.attr = attr;
			this.icon = icon;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public Attr getAttr() {
			return attr;
		}
		public void setAttr(Attr attr) {
			this.attr = attr;
		}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		@Override
	    public String toString(){
	        return new ToStringBuilder(this).append("title", title).toString();
	    }
	}
	
	private Data data;
	private Attr attr;
	private String state;
	private List<JsTreeNode> children;
	
	public JsTreeNode() {
		super();
	}
	public JsTreeNode(Data data, Attr attr, String state,
			List<JsTreeNode> children) {
		super();
		this.data = data;
		this.attr = attr;
		this.state = state;
		this.children = children;
	}

	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	public Attr getAttr() {
		return attr;
	}
	public void setAttr(Attr attr) {
		this.attr = attr;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<JsTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<JsTreeNode> children) {
		this.children = children;
	}
	public void addChild(JsTreeNode child){
		if(child!=null){
			if(this.children==null) this.children = new ArrayList<JsTreeNode>();
			this.children.add(child);
		}
	}
	public void flattenMe(List<JsTreeNode> nodes){
		nodes.add(this);
		if(this.children!=null && this.children.size()>0){
			for(JsTreeNode n : this.children){
				n.flattenMe(nodes);
			}
		}
	}
	
	
	// default is always based on alpha
	public static Comparator<JsTreeNode> leafFirstFolderLastComparator = new Comparator<JsTreeNode>() {

		public int compare(JsTreeNode node1, JsTreeNode node2) {
			// compare to attr.rel
			if(node1.getAttr()!=null && node1.getAttr().getRel()!=null && node1.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName()) 
					&& node2.getAttr()!=null && node2.getAttr().getRel()!=null && node2.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName())){ // node1 and node2 are folder
				return node1.getData().getTitle().compareToIgnoreCase(node2.getData().getTitle());
			}else if(node1.getAttr()!=null && node1.getAttr().getRel()!=null && node1.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName())){ // node1 is folder
				return 1;
			}else if(node2.getAttr()!=null && node2.getAttr().getRel()!=null && node2.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName())){ // node2 is folder
				return 0;
			}else{
				return node1.getData().getTitle().compareToIgnoreCase(node2.getData().getTitle());
			}
		}
	};
	
	// default is always based on alphabic
	public static Comparator<JsTreeNode> viewFirstFolderSecondLeafLastComparator = new Comparator<JsTreeNode>(){

		@Override
		public int compare(JsTreeNode node1, JsTreeNode node2) {
			// compare attr.rel
			if(node1.getAttr()!=null && node1.getAttr().getRel()!=null && node1.getAttr().getRel().equals(JsTreeNode.NodeType.instanceView.getSystemName())
					&& node2.getAttr()!=null && node2.getAttr().getRel()!=null && node2.getAttr().getRel().equals(JsTreeNode.NodeType.instanceView.getSystemName())){ // node1 and node2 are view
				return node1.getData().getTitle().compareToIgnoreCase(node2.getData().getTitle());
			}else if(node1.getAttr()!=null && node1.getAttr().getRel()!=null && node1.getAttr().getRel().equals(JsTreeNode.NodeType.instanceView.getSystemName())){ // node1 is view
				return 0;
			}else if(node2.getAttr()!=null && node2.getAttr().getRel()!=null && node2.getAttr().getRel().equals(JsTreeNode.NodeType.instanceView.getSystemName())){ // node2 is view
				return 1;
			}else if(node1.getAttr()!=null && node1.getAttr().getRel()!=null && node1.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName()) 
					&& node2.getAttr()!=null && node2.getAttr().getRel()!=null && node2.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName())){ // node1 and node2 are folder
				return node1.getData().getTitle().compareToIgnoreCase(node2.getData().getTitle());
			}else if(node1.getAttr()!=null && node1.getAttr().getRel()!=null && node1.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName())){ // node1 is folder
				return 0;
			}else if(node2.getAttr()!=null && node2.getAttr().getRel()!=null && node2.getAttr().getRel().equals(JsTreeNode.NodeType.folder.getSystemName())){ // node2 is folder
				return 1;
			}else{
				return node1.getData().getTitle().compareToIgnoreCase(node2.getData().getTitle());
			}
		}
		
	};
	
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("data",data).toString();
    }
}

package com.bizislife.core.controller.component;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.view.dto.AccountDto;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("entityNode")
public class EntityTreeNode implements TreeNode{

	private String prettyName;
	private String systemName;
	@XStreamImplicit
	private List<EntityTreeNode> subnodes;
	@XStreamOmitField
	private EntityDetail.EntityType type;
	@XStreamOmitField
	private String defaultImageSysName;
	@XStreamOmitField
	private Map<JsTreeNode.NodeType, Integer> childrenNumbers; // hold count numbers for different children. Folder number, product number, etc.
	@XStreamOmitField
	private String cssClassInfos; // this cssClassInfos will be added into jstreeNode's cssClass. Example of extraInfo: "sys notDel"
	
	@XStreamOmitField
	private String url; // this will be used in category page for category's url or product's url
	
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

	@JsonIgnore
	public List<EntityTreeNode> getSubnodes() {
		return subnodes;
	}

	public void setSubnodes(List<EntityTreeNode> subnodes) {
		this.subnodes = subnodes;
	}
	
	public void addSubnode(EntityTreeNode subnode){
		if(this.subnodes==null) this.subnodes = new ArrayList<EntityTreeNode>();
		this.subnodes.add(subnode);
	}
	
	public void addSubnodes(List<EntityTreeNode> newNodes){
		if(newNodes!=null && newNodes.size()>0){
			if(this.subnodes==null) this.subnodes = new ArrayList<EntityTreeNode>();
			for(EntityTreeNode n : newNodes){
				this.subnodes.add(n);
			}
		}
	}

	public EntityDetail.EntityType getType() {
		return type;
	}

	public void setType(EntityDetail.EntityType type) {
		this.type = type;
	}

	public String getDefaultImageSysName() {
		return defaultImageSysName;
	}

	public void setDefaultImageSysName(String defaultImageSysName) {
		this.defaultImageSysName = defaultImageSysName;
	}

	@JsonIgnore
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@JsonIgnore
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
	
	/**
	 * @return number of subfolders based on childrenNumbers map
	 */
	public int getTotalSubfolder(){
		if(childrenNumbers!=null && childrenNumbers.get(JsTreeNode.NodeType.folder)!=null){
			return childrenNumbers.get(JsTreeNode.NodeType.folder);
		}
		return 0;
	}
	
	/**
	 * @return number of products based on childrenNumbers map
	 */
	public int getTotalProducts(){
		if(childrenNumbers!=null && childrenNumbers.get(JsTreeNode.NodeType.instance)!=null){
			return childrenNumbers.get(JsTreeNode.NodeType.instance);
		}
		return 0;
	}
	
	/**
	 * assume that EntityTreeNode.type is filled
	 * 
	 * @param type
	 * @return
	 */
	public List<EntityTreeNode> findEntityTreeNodeByEntityType(EntityDetail.EntityType type){
		if(this.subnodes!=null && this.subnodes.size()>0 && type!=null){
			List<EntityTreeNode> results = new ArrayList<EntityTreeNode>();
			for(EntityTreeNode n : this.subnodes){
				if(n.type!=null && n.type.equals(type)){
					results.add(n);
				}
			}
			
			return results;
		}
		
		return null;
	}
	
	// easy way for jsp to get
	@JsonIgnore
	public List<EntityTreeNode> getDirectSubFolders(){
		return findEntityTreeNodeByEntityType(EntityDetail.EntityType.folder);
	}
	@JsonIgnore
	public List<EntityTreeNode> getDirectProducts(){
		return findEntityTreeNodeByEntityType(EntityDetail.EntityType.entity);
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

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("prettyName",prettyName).toString();
    }
	
	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(prettyName);
		Attr dataAttr = ret.new Attr();
//		dataAttr.setHref(url!=null?url:"/#");
//		data.setAttr(dataAttr);
//		if(StringUtils.isNotBlank(icon)){
//			data.setIcon(icon);
//		}else{
//			data.setIcon(defaultIcon);
//		}
		ret.setData(data);
		
		Attr attr = ret.new Attr();
		attr.setId(systemName!=null?systemName:UUID.randomUUID().toString());
//		if(foldOpenAfterLoad) attr.setFoldOpenAfterLoad(true);
		ret.setAttr(attr);
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		
		if(subnodes!=null && subnodes.size()>0){
			List<JsTreeNode> tns = new ArrayList<JsTreeNode>();
			for(EntityTreeNode n : subnodes){
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
			if(type!=null && type.equals(EntityDetail.EntityType.folder)){
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
			}else if(type!=null && type.equals(EntityDetail.EntityType.entity)){
				attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());

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
				attr.setRel(JsTreeNode.NodeType.leaf.getSystemName());
			}
		}
		
		return ret;
	}

}

package com.bizislife.core.controller.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.bizislife.core.controller.component.JsTreeNode.NodeType;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute;
import com.bizislife.core.siteDesign.module.ModuleEntityCategoryListAttribute.Type;
import com.bizislife.core.view.dto.AccountDto;

public class GeneralTreeNode implements TreeNode{
	
	private String prettyName;
	private String systemName;
	private String url;
	private List<GeneralTreeNode> subNodes;
	private JsTreeNode.NodeType nodetype; // folder or leaf
	private ModuleEntityCategoryListAttribute.Type catType; // product, image, ....
	private boolean selected;
	
	public GeneralTreeNode() {
		super();
	}

	public GeneralTreeNode(String prettyName, String systemName, String url,
			List<GeneralTreeNode> subNodes, NodeType nodetype, Type catType, boolean selected) {
		super();
		this.prettyName = prettyName;
		this.systemName = systemName;
		this.url = url;
		this.subNodes = subNodes;
		this.nodetype = nodetype;
		this.catType = catType;
		this.selected = selected;
	}

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<GeneralTreeNode> getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List<GeneralTreeNode> subNodes) {
		this.subNodes = subNodes;
	}
	
	public void addSubNode(GeneralTreeNode subNode){
		if(this.subNodes==null) this.subNodes = new ArrayList<GeneralTreeNode>();
		this.subNodes.add(subNode);
	}

	public JsTreeNode.NodeType getNodetype() {
		return nodetype;
	}

	public void setNodetype(JsTreeNode.NodeType nodetype) {
		this.nodetype = nodetype;
	}

	public ModuleEntityCategoryListAttribute.Type getCatType() {
		return catType;
	}

	public void setCatType(ModuleEntityCategoryListAttribute.Type catType) {
		this.catType = catType;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList,
			AccountDto currentAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "GeneralTreeNode [prettyName=" + prettyName + ", systemName="
				+ systemName + ", url=" + url + ", nodetype=" + nodetype + ", catType=" + catType + "]";
	}
	
	public void toPrintTree(int level){
		System.out.print(toString());
		System.out.println("");
		if(this.subNodes!=null && this.subNodes.size()>0){
			level = level + 9;
			for(int i=0; i<this.subNodes.size(); i++){
				for(int j=0; j<level; j++){
					System.out.print(" ");
				}
				this.subNodes.get(i).toPrintTree(level);
				
			}
		}
	}
	
	public EntityTreeNode transferToEntityTreeNode(boolean includeSubNodes){
		EntityTreeNode entityTreeNode = new EntityTreeNode();
		entityTreeNode.setPrettyName(this.prettyName);
		entityTreeNode.setSystemName(this.systemName);
		EntityDetail.EntityType theEntityType = null;
		if(this.nodetype!=null){
			if(this.nodetype.equals(JsTreeNode.NodeType.folder)){
				theEntityType = EntityDetail.EntityType.folder;
			}else if(this.nodetype.equals(JsTreeNode.NodeType.leaf)){
				theEntityType = EntityDetail.EntityType.entity;
			}
		}
		entityTreeNode.setType(theEntityType);
		entityTreeNode.setUrl(this.url);
		
		if(includeSubNodes){
			if(this.subNodes!=null && this.subNodes.size()>0){
				for(GeneralTreeNode tn : this.subNodes){
					EntityTreeNode sub_entityTreeNode = tn.transferToEntityTreeNode(includeSubNodes);
					entityTreeNode.addSubnode(sub_entityTreeNode);
				}
			}
		}
		
		return entityTreeNode;
	}
	
	// *** all comparator methods **** ////
	public static Comparator<GeneralTreeNode> prettyNameSort_ASC = new Comparator<GeneralTreeNode>(){
		@Override
		public int compare(GeneralTreeNode arg1, GeneralTreeNode arg2) {
			return arg1.getPrettyName().compareToIgnoreCase(arg2.getPrettyName());
		}
		
	};
	
	

}

package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.bizislife.core.controller.component.JsTreeNode;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.view.dto.AccountDto;

@Entity
@Table(name="instanceview")
public class InstanceView implements PojoInterface, TreeNode, Serializable{
	
	private static final long serialVersionUID = 2163137345979140737L;

	public static enum IsDefault{
		yes("1"),
		no("0"),
		;
		private String code;

		private IsDefault(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
		public static IsDefault fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(IsDefault v : IsDefault.values()){
					if(v.getCode().equals(code.trim())){
						return v;
					}
				}
			}
			return null;
		}
	}
	
	public static enum Type {
		instanceView("i"),
		NormalInstanceView("n"),
		ProductInstanceView("p"),
		;
		private String code;

		private Type(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
		
		public static Type fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(Type v : Type.values()){
					if(v.getCode().equals(code.trim())){
						return v;
					}
				}
			}
			return null;
		}
		
	}
	
	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="instanceviewuuid")
	private String instanceviewuuid;

	@Column(name="moduleuuid")
	private String moduleuuid;

	@Column(name="moduleinstanceuuid")
	private String moduleinstanceuuid;
	
	@Column(name="viewname")
	private String viewname;
	
	@Column(name="description")
	private String description;
	
	@Column(name="jsp")
	private String jsp;
	
	@Column(name="css")
	private String css;
	
	@Column(name="isdefault")
	private String isdefault;
	
	@Column(name="viewtype")
	private String viewtype;
	
	@Column(name="orgid")
	private Long orgid;

	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="creator_id")
	private Long creator_id;
	
	// childrenNumbers is used for switching to treeNode purpose
	@Transient
	private int childrenNumbers;
	
	// cssClassInfos is used for switching to treeNode purpose
	@Transient
	private String cssClassInfos;

	public InstanceView() {
		super();
	}

	public InstanceView(Long id, String instanceviewuuid, String moduleuuid,
			String moduleinstanceuuid, String viewname, String description, String jsp, String css,
			String isdefault, String viewtype, Long orgid, Date createdate, Long creator_id) {
		super();
		this.id = id;
		this.instanceviewuuid = instanceviewuuid;
		this.moduleuuid = moduleuuid;
		this.moduleinstanceuuid = moduleinstanceuuid;
		this.viewname = viewname;
		this.description = description;
		this.jsp = jsp;
		this.css = css;
		this.isdefault = isdefault;
		this.viewtype = viewtype;
		this.orgid = orgid;
		this.createdate = createdate;
		this.creator_id = creator_id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInstanceviewuuid() {
		return instanceviewuuid;
	}

	public void setInstanceviewuuid(String instanceviewuuid) {
		this.instanceviewuuid = instanceviewuuid;
	}

	public String getModuleuuid() {
		return moduleuuid;
	}

	public void setModuleuuid(String moduleuuid) {
		this.moduleuuid = moduleuuid;
	}

	public String getModuleinstanceuuid() {
		return moduleinstanceuuid;
	}

	public void setModuleinstanceuuid(String moduleinstanceuuid) {
		this.moduleinstanceuuid = moduleinstanceuuid;
	}

	public String getJsp() {
		return jsp;
	}

	public void setJsp(String jsp) {
		this.jsp = jsp;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public String getIsdefault() {
		return isdefault;
	}

	public void setIsdefault(String isdefault) {
		this.isdefault = isdefault;
	}

	public Long getOrgid() {
		return orgid;
	}

	public void setOrgid(Long orgid) {
		this.orgid = orgid;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Long creator_id) {
		this.creator_id = creator_id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getViewname() {
		return viewname;
	}

	public void setViewname(String viewname) {
		this.viewname = viewname;
	}

	public String getViewtype() {
		return viewtype;
	}

	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
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
		this.cssClassInfos = cssClassInfos;
	}

	public void addCssClassInfos(String cssClassInfos){
		if(StringUtils.isNotBlank(this.cssClassInfos)){
			this.cssClassInfos = this.cssClassInfos + " " + cssClassInfos;
		}else{
			this.cssClassInfos = cssClassInfos;
		}
		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("InstanceView [id=").append(id)
				.append(", instanceviewuuid=").append(instanceviewuuid)
				.append(", moduleuuid=").append(moduleuuid)
				.append(", moduleinstanceuuid=").append(moduleinstanceuuid)
				.append(", viewname=").append(viewname)
				.append(", description=").append(description)
				.append(", isdefault=").append(isdefault).append(", viewtype=")
				.append(viewtype).append(", orgid=").append(orgid)
				.append(", createdate=").append(createdate)
				.append(", creator_id=").append(creator_id).append("]");
		return builder.toString();
	}

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(this.viewname);
		//Attr dataAttr = ret.new Attr();
		
		ret.setData(data);
		
		Attr attr = ret.new Attr();
		attr.setId(this.instanceviewuuid);
		attr.setRel(InstanceView.Type.instanceView.name());
		ret.setAttr(attr);
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		
		if(childrenNumbers>0){
			ret.setState("closed");
		}else{
			ret.setState("");
		}
		
		return ret;
	}

	@Override
	public String getPrettyName() {
		return this.viewname;
	}

	@Override
	public String getSystemName() {
		return this.instanceviewuuid;
	}

}

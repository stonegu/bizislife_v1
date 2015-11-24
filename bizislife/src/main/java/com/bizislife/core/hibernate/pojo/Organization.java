package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.bizislife.core.controller.component.JsTreeNode;
import com.bizislife.core.controller.component.ModuleTreeNode;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.controller.component.JsTreeNode.Attr;
import com.bizislife.core.controller.component.JsTreeNode.Data;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.definition.DatabaseRelatedCode;


@Entity
@Table(name="organization")
public class Organization implements PojoInterface, TreeNode, Serializable{

	private static final long serialVersionUID = 7510593674467039326L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="orgname")
	private String orgname;
	
	@Column(name="orgsysname")
	private String orgsysname;
	
	@Column(name="orguuid")
	private String orguuid;

	@Column(name="salt")
	private String salt;

	@Column(name="orglevel")
	private String orglevel;

	@Column(name="createdate")
	private Date createdate;
    
	@Column(name="activatedate")
	private Date activatedate;
	
	@Column(name="suspenddate")
	private Date suspenddate;

	@Column(name="deletedate")
	private Date deletedate;

	@Column(name="creator_id")
	private Long creator_id;
	
	@Column(name="rootentityid")
	private String rootentityid;
	
	@Transient
	private String cssClassInfos;
	
	@Transient
	private Map<JsTreeNode.NodeType, Integer> childrenNumbers;
	
	public Organization() {
		super();
	}

	public Organization(Long id, String orgname, String orgsysname, String orguuid, String orglevel,
			Date createdate, Date activatedate, Date suspenddate, Date deletedate,
			Long creator_id, String rootentityid) {
		super();
		this.id = id;
		this.orgname = orgname;
		this.orgsysname = orgsysname;
		this.orguuid = orguuid;
		this.orglevel = orglevel;
		this.createdate = createdate;
		this.activatedate = activatedate;
		this.suspenddate = suspenddate;
		this.deletedate = deletedate;
		this.creator_id = creator_id;
		this.rootentityid = rootentityid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	/**
	 * @return org's systemName (not the uuid)
	 */
	public String getOrgsysname() {
		return orgsysname;
	}

	public void setOrgsysname(String orgsysname) {
		this.orgsysname = orgsysname;
	}

	public String getOrguuid() {
		return orguuid;
	}

	public void setOrguuid(String orguuid) {
		this.orguuid = orguuid;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getOrglevel() {
		return orglevel;
	}

	public void setOrglevel(String orglevel) {
		this.orglevel = orglevel;
	}
	
	public boolean isBizOrg(){
		return this.orglevel!=null?this.orglevel.trim().equals(DatabaseRelatedCode.OrganizationRelated.orglevel_bizislife.getCode())?true:false:false;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Date getActivatedate() {
		return activatedate;
	}

	public void setActivatedate(Date activatedate) {
		this.activatedate = activatedate;
	}

	public Date getSuspenddate() {
		return suspenddate;
	}

	public void setSuspenddate(Date suspenddate) {
		this.suspenddate = suspenddate;
	}

	public Date getDeletedate() {
		return deletedate;
	}

	public void setDeletedate(Date deletedate) {
		this.deletedate = deletedate;
	}

	public Long getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(Long creator_id) {
		this.creator_id = creator_id;
	}

	public String getRootentityid() {
		return rootentityid;
	}

	public void setRootentityid(String rootentityid) {
		this.rootentityid = rootentityid;
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
	
	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("name",orgname).toString();
    }

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(this.orgsysname);
		Attr dataAttr = ret.new Attr();
		ret.setData(data);
		
		Attr attr = ret.new Attr();
		attr.setId(this.orguuid);
		ret.setAttr(attr);
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		

		attr.setRel(JsTreeNode.NodeType.org.getSystemName());
		
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
		
		return ret;
	}

	@Override
	public String getPrettyName() {
		return this.orgname;
	}

	@Override
	public String getSystemName() {
		return this.orguuid;
	}



}

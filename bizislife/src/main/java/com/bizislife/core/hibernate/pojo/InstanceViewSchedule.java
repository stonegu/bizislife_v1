package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

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
import com.bizislife.core.hibernate.pojo.ContainerModuleSchedule.PriorityLevel;
import com.bizislife.core.view.dto.AccountDto;

@Entity
@Table(name="instanceviewschedule")
public class InstanceViewSchedule implements PojoInterface, ScheduleInterface, TreeNode, Serializable{

	/**
	 * higher number -> higher priority
	 *
	 */
	public static enum PriorityLevel{
		level0(0),
		level1(1),
		level2(2),
		level3(3),
		level4(4),
		level5(5),
		level6(6),
		level7(7),
		level8(8),
		level9(9),
		;
		
		private int code;

		private PriorityLevel(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public static PriorityLevel fromCode(int k){
			if(k>-1 && k<10){
				for(PriorityLevel p : PriorityLevel.values()){
					if(p.getCode()==k){
						return p;
					}
				}
			}
			
			return null;
		}
		
	}
	
	
	
	
	private static final long serialVersionUID = 5849548284220490572L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="uuid")
	private String uuid;

	@Column(name="moduleinstanceuuid")
	private String moduleinstanceuuid;

	@Column(name="instanceviewuuid")
	private String instanceviewuuid;

	@Column(name="schedulename")
	private String schedulename;
	
	@Column(name="startdate")
	private Date startdate;
	
	@Column(name="enddate")
	private Date enddate;
	
	@Column(name="createdate")
	private Date createdate;
	
	@Column(name="priority")
	private Integer priority;
	
	// cssClassInfos is used for switching to treeNode purpose
	@Transient
	private String cssClassInfos;

	public InstanceViewSchedule() {
		super();
	}

	public InstanceViewSchedule(Long id, String schedulename, String uuid,
			String moduleinstanceuuid, String instanceviewuuid, Date startdate,
			Date enddate, Date createdate, Integer priority) {
		super();
		this.id = id;
		this.schedulename = schedulename;
		this.uuid = uuid;
		this.moduleinstanceuuid = moduleinstanceuuid;
		this.instanceviewuuid = instanceviewuuid;
		this.startdate = startdate;
		this.enddate = enddate;
		this.createdate = createdate;
		this.priority = priority;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getModuleinstanceuuid() {
		return moduleinstanceuuid;
	}

	public void setModuleinstanceuuid(String moduleinstanceuuid) {
		this.moduleinstanceuuid = moduleinstanceuuid;
	}

	public String getInstanceviewuuid() {
		return instanceviewuuid;
	}

	public void setInstanceviewuuid(String instanceviewuuid) {
		this.instanceviewuuid = instanceviewuuid;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getSchedulename() {
		return schedulename;
	}

	public void setSchedulename(String schedulename) {
		this.schedulename = schedulename;
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
		builder.append("InstanceViewSchedule [id=").append(id)
				.append(", uuid=").append(uuid).append(", moduleinstanceuuid=")
				.append(moduleinstanceuuid).append(", instanceviewuuid=")
				.append(instanceviewuuid).append(", schedulename=")
				.append(schedulename).append(", startdate=").append(startdate)
				.append(", enddate=").append(enddate).append(", createdate=")
				.append(createdate).append(", priority=").append(priority)
				.append("]");
		return builder.toString();
	}

	@Override
	public JsTreeNode switchToJsTreeNode(String path, String[] checkedNodeList, AccountDto currentAccount) {
		
		JsTreeNode ret = new JsTreeNode();
		
		Data data = ret.new Data();
		data.setTitle(this.schedulename);
		Attr dataAttr = ret.new Attr();
		
		ret.setData(data);
		
		Attr attr = ret.new Attr();
		attr.setId(this.uuid);
		attr.setRel("schedule");
		ret.setAttr(attr);
		
		if(StringUtils.isNotBlank(cssClassInfos)){
			attr.addCssClass(cssClassInfos);
		}
		
		ret.setState("");
		
		return ret;
	}

	@Override
	public String getPrettyName() {
		return this.schedulename;
	}

	@Override
	public String getSystemName() {
		return this.uuid;
	}


}

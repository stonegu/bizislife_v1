package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="containermoduleschedule")
public class ContainerModuleSchedule implements PojoInterface, ScheduleInterface, Serializable{
	
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
	
	
	

	private static final long serialVersionUID = -2834582888096626781L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;
	
	@Column(name="uuid")
	private String uuid;

	@Column(name="containeruuid")
	private String containeruuid;

	@Column(name="moduleuuid")
	private String moduleuuid;
	
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

	public ContainerModuleSchedule() {
		super();
	}

	public ContainerModuleSchedule(Long id, String schedulename, String uuid, String containeruuid,
			String moduleuuid, Date startdate, Date enddate, Date createdate, Integer priority) {
		super();
		this.id = id;
		this.schedulename = schedulename;
		this.uuid = uuid;
		this.containeruuid = containeruuid;
		this.moduleuuid = moduleuuid;
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

	public String getContaineruuid() {
		return containeruuid;
	}

	public void setContaineruuid(String containeruuid) {
		this.containeruuid = containeruuid;
	}

	public String getModuleuuid() {
		return moduleuuid;
	}

	public void setModuleuuid(String moduleuuid) {
		this.moduleuuid = moduleuuid;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContainerModuleSchedule [id=").append(id)
				.append(", uuid=").append(uuid).append(", containeruuid=")
				.append(containeruuid).append(", moduleuuid=")
				.append(moduleuuid).append(", schedulename=")
				.append(schedulename).append(", startdate=").append(startdate)
				.append(", enddate=").append(enddate).append(", createdate=")
				.append(createdate).append(", priority=").append(priority)
				.append("]");
		return builder.toString();
	}

	
}

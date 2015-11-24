package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;



@Entity
@Table(name="message")
public class Message implements PojoInterface, Serializable{

	public static enum MsgType{
		topicPost("01"),
		memberSend("02"),
		;
		
		private String code; // max len is 2
		private MsgType(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
		public static MsgType fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(MsgType mt : MsgType.values()){
					if(mt.getCode().equals(code.trim())){
						return mt;
					}
				}
			}
			return null;
		}
	}
	
	public static enum MsgStatus{
		unread("0"),
		read("1"),

		unreadFlagged("5"), // unread(0) + 5 is flagged 
		readFlagged("6"), // read(1) + 5 is flagged
		
		;
		
		private String code;
		private MsgStatus(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
		public static MsgStatus fromCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(MsgStatus mt : MsgStatus.values()){
					if(mt.getCode().equals(code.trim())){
						return mt;
					}
				}
			}
			return null;
		}
	}
	
	private static final long serialVersionUID = -8643646099105041462L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="account_id")
	private Long account_id;

	@Column(name="accountuuid")
	private String accountuuid;
	
	@Column(name="org_id")
	private Long org_id;

	@Column(name="from_id")
	private Long from_id;

	@Column(name="from_name")
	private String from_name;

	@Column(name="title")
	private String title;

	@Column(name="msgtype")
	private String msgtype;

	@Column(name="msgstatus")
	private String msgstatus;

	@Column(name="createdate")
	private Date createdate;

	@ManyToOne
	private Msgbody msgbody;

	@ManyToOne
	private ActivityLog activityLog;

	public Message() {
		super();
	}

	public Message(Long id, Long account_id, String accountuuid, Long org_id, Long from_id, String from_name,
			String title, String msgtype, String msgstatus,
			Date createdate, Msgbody msgbody, ActivityLog activityLog) {
		super();
		this.id = id;
		this.account_id = account_id;
		this.accountuuid = accountuuid;
		this.org_id = org_id;
		this.from_id = from_id;
		this.from_name = from_name;
		this.title = title;
		this.msgtype = msgtype;
		this.msgstatus = msgstatus;
		this.createdate = createdate;
		this.msgbody = msgbody;
		this.activityLog = activityLog;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(Long account_id) {
		this.account_id = account_id;
	}

	public Long getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Long org_id) {
		this.org_id = org_id;
	}

	public Long getFrom_id() {
		return from_id;
	}

	public void setFrom_id(Long from_id) {
		this.from_id = from_id;
	}

	public String getFrom_name() {
		return from_name;
	}

	public void setFrom_name(String from_name) {
		this.from_name = from_name;
	}

	public String getAccountuuid() {
		return accountuuid;
	}

	public void setAccountuuid(String accountuuid) {
		this.accountuuid = accountuuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}

	public String getMsgstatus() {
		return msgstatus;
	}

	public void setMsgstatus(String msgstatus) {
		this.msgstatus = msgstatus;
	}

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	public Msgbody getMsgbody() {
		return msgbody;
	}

	public void setMsgbody(Msgbody msgbody) {
		this.msgbody = msgbody;
	}

	public ActivityLog getActivityLog() {
		return activityLog;
	}

	public void setActivityLog(ActivityLog activityLog) {
		this.activityLog = activityLog;
	}
	
	public static Comparator<Message> createDateDescendComparator = new Comparator<Message>() {
		@Override
		public int compare(Message m1, Message m2) {
			return m2.getCreatedate().compareTo(m1.getCreatedate());
		}
	};
	
	
	

	@Override
    public String toString(){
        return new ToStringBuilder(this).append("id", id).append("title", title).toString();
    }
}

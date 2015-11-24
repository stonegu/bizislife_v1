package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;

@Entity
@Table(name="pagemeta")
public class PageMeta implements PojoInterface, Serializable{
	
	public static enum DefaultCss{
		previewMode("1", "System will generate most of the css definition based on your page design"),
		easyMode("2", "System will only generate postion css definition based on your page design"),
		customizedMode("3", "System will not generate any css definition based on your page design, you can write all your own css based on your design."),
		purecss("4", "System will generate css based on purecss.io, a set of small, responsive CSS modules. Note: make sure the max column in one row can't exceed 24!"),
		;
		
		private String code;
		private String desc;
		private DefaultCss(String code, String desc) {
			this.code = code;
			this.desc = desc;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		
		static public DefaultCss getDefaultCssForCode(String code){
			if(StringUtils.isNotBlank(code)){
				for(DefaultCss c : DefaultCss.values()){
					if(c.getCode().equals(code)){
						return c;
					}
				}
			}
			return null;
		}
		
	}
	
	private static final long serialVersionUID = 5561856046411142674L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="pagemetauuid")
	private String pagemetauuid;

	@Column(name="pageuuid")
	private String pageuuid;
	
	@Column(name="title")
	private String title;
	
	@Column(name="defaultcss")
	private String defaultcss;
	
	@Column(name="css")
	private String css;
	
	@Column(name="headcontent")
	private String headcontent;

	@Column(name="changelist")
	private String changelist;

	public PageMeta() {
		super();
	}

	public PageMeta(Long id, String pagemetauuid, String pageuuid,
			String title, String defaultcss, String css, String changelist) {
		super();
		this.id = id;
		this.pagemetauuid = pagemetauuid;
		this.pageuuid = pageuuid;
		this.changelist = changelist;
		this.defaultcss = defaultcss;
		this.css = css;
		this.headcontent = headcontent;
		this.title = title;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPagemetauuid() {
		return pagemetauuid;
	}

	public void setPagemetauuid(String pagemetauuid) {
		this.pagemetauuid = pagemetauuid;
	}

	public String getPageuuid() {
		return pageuuid;
	}

	public void setPageuuid(String pageuuid) {
		this.pageuuid = pageuuid;
	}

	public String getChangelist() {
		return changelist;
	}

	public void setChangelist(String changelist) {
		this.changelist = changelist;
	}

	public String getDefaultcss() {
		return defaultcss;
	}

	public void setDefaultcss(String defaultcss) {
		this.defaultcss = defaultcss;
	}

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public String getHeadcontent() {
		return headcontent;
	}

	public void setHeadcontent(String headcontent) {
		this.headcontent = headcontent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PageMeta [id=");
		builder.append(id);
		builder.append(", pagemetauuid=");
		builder.append(pagemetauuid);
		builder.append(", pageuuid=");
		builder.append(pageuuid);
		builder.append(", changelist=");
		builder.append(changelist);
		builder.append(", defaultcss=");
		builder.append(defaultcss);
		builder.append("]");
		return builder.toString();
	}
	

}
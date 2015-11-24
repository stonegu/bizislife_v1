package com.bizislife.core.siteDesign.module;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.bizislife.core.entity.converter.ModuleAttrConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("moduleImageAttr")
@XStreamConverter(ModuleAttrConverter.class)
public class ModuleImageAttribute extends ModuleAttribute{
	
	public static final String MODULEATTRIBUTEDESC = "Image attribute";
	
	private static final long serialVersionUID = -3784480993119736850L;

	private String fileSystemName;
	private Boolean defaultPicture; // this moduleImageAttribute will be the default picture for entity (for product only)!

	public ModuleImageAttribute() {
		super();
	}

	public ModuleImageAttribute(String uuid, String name, String title,
			Boolean visibility, String documentation, Boolean required,
			Boolean array, String fileSystemName) {
		super(uuid, name, title, visibility, documentation, required, array);
		this.fileSystemName = fileSystemName;
	}

	@JsonIgnore
	public String getFileSystemName() {
		return fileSystemName;
	}

	public void setFileSystemName(String fileSystemName) {
		this.fileSystemName = fileSystemName;
	}
	
	//another method to get fileSystemName for easy jsp access!!
	public String getValue(){
		return fileSystemName;
	}
	
	public String getImg_original(){
		return "/getphoto?id="+this.fileSystemName+"&size=-1";
	}
	public String getImg_50(){
		return "/getphoto?id="+this.fileSystemName+"&size=50";
	}
	public String getImg_100(){
		return "/getphoto?id="+this.fileSystemName+"&size=100";
	}
	public String getImg_200(){
		return "/getphoto?id="+this.fileSystemName+"&size=200";
	}
	public String getImg_600(){
		return "/getphoto?id="+this.fileSystemName+"&size=600";
	}

	public Boolean getDefaultPicture() {
		return defaultPicture;
	}

	public void setDefaultPicture(Boolean defaultPicture) {
		this.defaultPicture = defaultPicture;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleImageAttribute [fileSystemName=");
		builder.append(fileSystemName);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((defaultPicture == null) ? 0 : defaultPicture.hashCode());
		result = prime * result
				+ ((fileSystemName == null) ? 0 : fileSystemName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModuleImageAttribute other = (ModuleImageAttribute) obj;
		if (defaultPicture == null) {
			if (other.defaultPicture != null)
				return false;
		} else if (!defaultPicture.equals(other.defaultPicture))
			return false;
		if (fileSystemName == null) {
			if (other.fileSystemName != null)
				return false;
		} else if (!fileSystemName.equals(other.fileSystemName))
			return false;
		return true;
	}
	

}

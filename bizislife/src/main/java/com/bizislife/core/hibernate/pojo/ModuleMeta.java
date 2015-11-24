package com.bizislife.core.hibernate.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="modulemeta")
public class ModuleMeta implements PojoInterface, Serializable{

	private static final long serialVersionUID = -4432372279073410868L;

	@Id
	@Column(name="id")
	@GeneratedValue
	private Long id;

	@Column(name="uuid")
	private String uuid;

	@Column(name="targetuuid")
	private String targetuuid;

	@Column(name="metadata")
	private String metadata;

	public ModuleMeta() {
		super();
	}

	public ModuleMeta(Long id, String uuid, String targetuuid,
			String metadata) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.targetuuid = targetuuid;
		this.metadata = metadata;
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

	public String getTargetuuid() {
		return targetuuid;
	}

	public void setTargetuuid(String targetuuid) {
		this.targetuuid = targetuuid;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModuleMeta [id=");
		builder.append(id);
		builder.append(", uuid=");
		builder.append(uuid);
		builder.append(", targetuuid=");
		builder.append(targetuuid);
		builder.append(", metadata=");
		builder.append(metadata);
		builder.append("]");
		return builder.toString();
	}
	
	
	
}

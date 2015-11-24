package com.bizislife.core.controller.component;

import java.util.ArrayList;
import java.util.List;

public class ContainerForPageBuilder {
	List<ContainerForPageBuilder> subContainers;
	private String prettyName;
	private String systemName;
	private String direction;
	private Integer topposition;
	private Integer leftposition;
	private Integer width;
	private Integer height;
	private boolean deletable;
	private boolean editable;
	
	public List<ContainerForPageBuilder> getSubContainers() {
		return subContainers;
	}
	public void setSubContainers(List<ContainerForPageBuilder> subContainers) {
		this.subContainers = subContainers;
	}
	public void addSubContainer(ContainerForPageBuilder container){
		if(subContainers==null) subContainers = new ArrayList<ContainerForPageBuilder>();
		subContainers.add(container);
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
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public Integer getTopposition() {
		return topposition;
	}
	public void setTopposition(Integer topposition) {
		this.topposition = topposition;
	}
	public Integer getLeftposition() {
		return leftposition;
	}
	public void setLeftposition(Integer leftposition) {
		this.leftposition = leftposition;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
	public boolean isDeletable() {
		return deletable;
	}
	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ContainerForPageBuilder [subContainers=")
				.append(subContainers).append(", prettyName=")
				.append(prettyName).append(", systemName=").append(systemName)
				.append(", direction=").append(direction)
				.append(", topposition=").append(topposition)
				.append(", leftposition=").append(leftposition)
				.append(", width=").append(width).append(", height=")
				.append(height).append(", deletable=").append(deletable)
				.append(", editable=").append(editable).append("]");
		return builder.toString();
	}

	
}

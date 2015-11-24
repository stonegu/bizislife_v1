package com.bizislife.core.controller.component;

public class PaginationNode {

	public static enum PaginationNodeType{
		goFirst, // <<
		goPrevious, // <
		goNext, // >
		goLast, // >>
		dotdotdot, // ...
		number, // 1, 2, 3, ..
		;
	}
	
	private String prettyName; // name to display, "page 1" or "p1" or ...
	private String url;
	private String title; // title for the <a> tag
	private PaginationNodeType type; 
	private boolean currentNode; // is current select node
	
	public PaginationNode() {
		super();
	}
	
	public PaginationNode(String prettyName, String url, String title,
			PaginationNodeType type, boolean currentNode) {
		super();
		this.prettyName = prettyName;
		this.url = url;
		this.title = title;
		this.type = type;
		this.currentNode = currentNode;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public void setPrettyName(String prettyName) {
		this.prettyName = prettyName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public PaginationNodeType getType() {
		return type;
	}

	public void setType(PaginationNodeType type) {
		this.type = type;
	}

	public boolean isCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(boolean currentNode) {
		this.currentNode = currentNode;
	}

	@Override
	public String toString() {
		return "PageNode [prettyName=" + prettyName + ", url=" + url
				+ ", title=" + title + ", type=" + type + ", currentNode="
				+ currentNode + "]";
	}
	
}

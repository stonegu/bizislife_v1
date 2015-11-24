package com.bizislife.core.controller.component;

import java.util.*;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FileUploadDto {
	
	private String targetNodeId; // which folder in the media tree that files uploaded
	
	// for normal multi-files upload form commit 
	private List<CommonsMultipartFile> files;
	
	public List<CommonsMultipartFile> getFiles() {
		return files;
	}

	public void setFiles(List<CommonsMultipartFile> files) {
		this.files = files;
	}

	public String getTargetNodeId() {
		return targetNodeId;
	}

	public void setTargetNodeId(String targetNodeId) {
		this.targetNodeId = targetNodeId;
	}

}

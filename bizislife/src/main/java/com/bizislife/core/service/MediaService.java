package com.bizislife.core.service;

import java.util.List;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;

public interface MediaService {
	
	/**
	 * @param mtype : return all types of medias if mtype is null
	 * @param folderId
	 * @return
	 */
	public List<MediaDetail> findMediasInFolder(String targetUuid, MediaType mtype, String folderId);
	
	public List<MediaDetail> findMediasInOrg(String targetUuid, MediaType mtype, Long orgid);
	
	public MediaDetail getMediaDetailByUuid(String uuid);
	
	public String newMediaNode(MediaType nodeType, String parentNodeUuid, String nodeName, String nodeUuid);
	
	public ApiResponse updateMediaDetailByFieldnameValue(String mediaUuid, String updateValueName, String updateValue);


}

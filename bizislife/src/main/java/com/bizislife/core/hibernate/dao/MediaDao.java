package com.bizislife.core.hibernate.dao;

import java.util.List;

import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.EntityTreeLevelView;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.hibernate.pojo.MediaTreeLevelView;
import com.bizislife.core.hibernate.pojo.NodeDetail;

public interface MediaDao {
	
	public int countAllMediasUnderCurrentFolderByCurrentFolderUuid(String uuid, MediaDetail.MediaType type);
	
	public int countSubFoldersByCurrentFolderUuid(String uuid);
	
	public void delMediaDetailById(Long id);
	
	public void delMediaTreeLevelView(Long id);
	
	public List<NodeDetail> findMedialDetailsInFolder(String folderUuid, boolean includeFolder, boolean includeLeaf);
	
	public List<NodeDetail> findMedialDetailsUnderFolder(String folderUuid);
	
	public List<NodeDetail> findMediasWithTypeInOrg(MediaType mtype, Long orgid);
	
	public List<MediaTreeLevelView> findOrgMediaTreeLevelViews(Long orgId);
	
	public MediaDetail getMediaDetailByUuid(String uuid);

	public MediaDetail getMediaTreeRoot(Long orgId);
	
	public MediaTreeLevelView getMediaTreeLevelViewByParentUuid(String parentNodeUuid);
	
	public MediaTreeLevelView getMediaTreeLevelViewHasNode(String nodeUuid);

	public MediaTreeLevelView getMediaTreeRootViewLevel(Long orgId);
	
	public Long saveMediaDetail(MediaDetail mediaDetail);
	
	public Long saveMediaTreeLevelView(MediaTreeLevelView mediaTreeLevelView);
}

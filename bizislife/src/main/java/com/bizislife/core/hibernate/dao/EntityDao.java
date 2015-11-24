package com.bizislife.core.hibernate.dao;

import java.util.*;

import com.bizislife.core.hibernate.pojo.*;

public interface EntityDao {
	
	public int countAllProductsUnderCurrentFolderByCurrentFolderUuid(String folderUuid, EntityDetail.EntityType type, boolean oneLevel, String sqlQueryToUse);
	
	public int countSubFoldersByCurrentFolderUuid(String folderUuid, boolean oneLevel);
	
	public void delEntityDetailById(Long id);
	
	public void delEntityTreeLevelView(Long id);
	
	public List<NodeDetail> findEntityDetailsInFolder(String folderUuid, boolean includeFold, boolean includeLeaf);
	
	public List<NodeDetail> findEntityDetailsUnderFolder(String folderNodeUuid);
	
	/**
	 * @param type type of result need to be return
	 * @param folderNodeUuid 
	 * @param totalResultsNum max number of results need to be return
	 * @param beginingIdx from idx number (>=beginingIdx)
	 * @param oneLevel true: only the direct children
	 * @return
	 */
	public List<EntityDetail> findEntityDetailsUnderFolderFromIdToTotalReturnNumber(EntityDetail.EntityType type, String folderNodeUuid, 
			int totalResultsNum, Long beginingIdx, 
			boolean oneLevel, boolean descending);

	public List<NodeDetail> findEntityDetailsUnderFolderFromOffsetToTotalReturnNumber(EntityDetail.EntityType type, String folderNodeUuid, 
			int totalResultsNum, int offset, 
			boolean oneLevel, boolean descending,
			String sqlQueryToUse);
	
	public List<EntityDetail> findEntitiesUsingModule(String moduleUuid);
	
	/**
	 * @param orgId
	 * @param entityType type of entityDetails need to be returned. if null, then return all.
	 * @return
	 */
	public List<EntityDetail> findOrgEntityDetails(Long orgId, EntityDetail.EntityType entityType);
	
	public List<EntityTreeLevelView> findOrgEntityTreeLevelViews(Long orgId);
	
	public EntityDetail getEntityDetailByUuid(String uuid);
	
	public EntityTreeLevelView getEntityTreeLevelViewByParentUuid(String parentUuid);
	
	public EntityTreeLevelView getEntityTreeLevelViewHasNode(String nodeuuid);
	
	public EntityDetail getProductTreeRoot(Long orgId);
	
	public EntityTreeLevelView getProductTreeRootViewLevel(Long orgId);
	
//	public EntityTreeLevelView getProductTreeViewLevel(String parentNodeUuid);
	
	public Long saveEntityDetail(EntityDetail entityDetail);
	
	public Long saveEntityTreeLevelView(EntityTreeLevelView entityTreeLevelView);

}

package com.bizislife.core.service;

import java.util.List;
import java.util.Map;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.entity.Entity;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.ModuleDetail;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.EntityDetail.EntityType;
import com.bizislife.core.hibernate.pojo.Permission;

public interface ProductService {
	
	/**
	 * This method only works in clone node in same tree. not from another tree (user TreeService.copyTreeNodeToAnotherTree)
	 * 
	 * @param nodeType
	 * @param parentNodeUuid
	 * @param nodeName
	 * @param cloneFromUuid
	 * @return
	 */
	public ApiResponse cloneProductNode(EntityDetail.EntityType nodeType, String parentNodeUuid, String nodeName, String cloneFromUuid);
	
	public int countProductsInFolderBySqlSegmentResponse(ApiResponse sqlSegmentResponse, String folderUuid, boolean includeFold, boolean includeLeaf);
	
	public String delAttribute(String entityUuid, String attrUuid);
	
	public ApiResponse deleteModuleSelectionForEntity(String entityuuid);
	
	/**
	 * this method is deprecated, use deleteModuleSelectionForEntity instead.
	 * 
	 * empty all module information for the entity, (empty moduleUuid, empty detail). 
	 * delete all views, shedules, jsp & css files.
	 * 
	 * @param productDetailUuid
	 */
	@Deprecated
	public void emptyProductDetailByUuid(String productDetailUuid);
	
	
	
	/**
	 * @param targetUuid user or group uuid
	 * @param folderUuid
	 * @return
	 */
	public List<NodeDetail> findProductsInfolder(String targetUuid, String folderUuid);
	
	/**
	 * @param mergedPermission for account or group
	 * @param folderUuid
	 * @param totalNumProductsInPage -1 for max # of product
	 * @param offset -1 for no offset
	 * @return
	 */
	public List<NodeDetail> findProductsInFolderByMergedPermission(Permission mergedPermission, String folderUuid, int totalNumProductsInPage, int offset, boolean includeFold, boolean includeLeaf);
	

	public List<NodeDetail> findProductsInFolderBySqlSegmentResponse(ApiResponse sqlSegmentResponse, String folderUuid, int totalNumProductsInPage, int offset, boolean includeFold, boolean includeLeaf);
	
	
	/**
	 * this method is to list products (no folder) for target (user or group) who has preview permission
	 * 
	 * @param targetUuid user or group uuid
	 * @param entityType the type of entityDetail need to be found. folder or product
	 * @param folderUuid find everything in this folder
	 * @param totalNumProductsInPage max number of subfolder and product can be under the folder. -1 for all
	 * @param pageIdx which page. if(pageIdx<=0) pageIdx = 1;
	 * @return
	 */
	public List<NodeDetail> findProductsInfolderForPreview(String targetUuid, String folderUuid, int totalNumProductsInPage, int pageIdx);
	
	/**
	 * this method is used for entity to get default or scheduled (Todo) pageuuid (categoryPageUuid for folder, productPageUuid for product) 
	 * 
	 * @param entityUuid get default categoryPageUuid or productPageUuid from this entity, 
	 * @param pagetype this pagetype (category or product) determines which pageuuid (category or product) should be returned
	 * @param sitetype this sitetype determines which pageuuid (desktop or mobile) should be returned
	 * @param upperDefinedCategoryPageUuid this is the easy way to get categoryPageUuid for entity: entity doesn't need to search parent's entity again to find pageuuid
	 * @param upperDefinedProductPageUuid this is the easy way to get productPageUuid for entity: entity doesn't need to search parent's entity again to find pageuuid
	 * @return
	 */
	public String getCategoryOrProductPageUuidForEntityNode(String entityUuid, PageDetail.Type pagetype, PageDetail.Type sitetype, String upperDefinedCategoryPageUuid, String upperDefinedProductPageUuid);
	
	/**
	 * @param productUuid
	 * @return closest folder which has module defined.
	 */
	public EntityDetail getClosestFolderHasModuleDefined(String productUuid);
	
	/**
	 * @param nodeUuid
	 * @return EntityDetail
	 */
	public EntityDetail getEntityDetailByUuid(String nodeUuid);
	
	@Deprecated
	public Entity getEntityFromXML(EntityType type, String entityInXML);
	
	/**
	 * this method is to generate url for entity (folder or product) 
	 * 
	 * @param entityNode
	 * @param sitetype desktop or mobile
	 * @param orguuid
	 * @param categoryPageUuid the category's page should be used for current node if current node is folder
	 * @param productPageUuid the product's page should be used for current node if current node is product
	 * @return url
	 */
	public String getEntityUrl(String hostname, EntityTreeNode entityNode, PageDetail.Type sitetype, String orguuid, String categoryPageName, String productPageName);
	
	/**
	 * @param nodeUuid
	 * @return Entity (ProductEntity, FolderEntity, ...)
	 */
	@Deprecated
	public Entity getEntityWithAttrsByNodeUuid(String nodeUuid);
	
	@Deprecated
	public String getXmlFromEntity(Entity entity);
	
	@Deprecated
	public String newAttribute(String productUuid, String attrType, String attrName, String attrValue);
	
	public String newProductNode(EntityType nodeType, String parentNodeUuid, String nodeName);

	public Long saveEntityDetail(EntityDetail entityDetail);
	
	public ApiResponse setDefaultPageForEntity(String entityUuid, String pageUuid, PageDetail.Type thePageType, PageDetail.Type theSiteType);
	
	@Deprecated
	public String updateAttribute(String productUuid, String attrType, String attrName, String attrValue, String attrUuid);
	
	public ApiResponse updateEntityDetailValue(String entityUuid, String updateValueName, String updateValue);
}

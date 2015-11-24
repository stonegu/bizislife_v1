package com.bizislife.core.service;

import java.util.List;
import java.util.Map;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.controller.component.TreeNode;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.OrgCanJoin;
import com.bizislife.core.hibernate.pojo.PageDetail;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.PermissionedStuff;
import com.bizislife.core.hibernate.pojo.PojoInterface;
import com.bizislife.core.hibernate.pojo.Permission.Type;

public interface PermissionService {
	
    public Permission addGroupPermission(Long groupId, Permission permission);
    
	public void addPermissionedStuffsToPermission(Long permissionId, List<PermissionedStuff> pstuffs);
	
	/**
	 * <strong>This method will be used for sharing product in different organizations only NOW!!!</strong>, <br/>
	 * the reason is : the business logic is not fully test and understand yet! <br/>
	 * <br/>
	 * 
	 * if you like to set product to be preview enable, you need to set product's moduleDetail to be preview enable too. <br/>
	 * if you like to set product to be read enable, you need to set product's moduleDetail to be read enable too. <br/>
	 * if you like to set product to be copy enable, you need to set product's moduleDetail to be copy enable too. <br/>
	 * if you like to set product to be modify enable, you need to set product's moduleDetail to be modify enable too. <br/>
	 * otherwise, product will not be preview enable, read enable, ..., even you set product's preview, read, ..., enabled. <br/>
	 * Also, <br/>
	 * if the permission is group permission for Group A, you need to check permissions for Group A, Everyone Group. <br/>
	 * if the permission is account permission for Account A, you need to check permissions for Account A, all Account A's groups. <br/> 
	 * 
	 * @param permissionType type of permission need to be check
	 * @param entityDetail
	 * @param permission
	 * @return GeneralSelectionType : <br/> 
	 * 			key - entityDetail.entityuuid, <br/>
	 * 			value - string of description for the result, <br/>
	 * 			selected - null for nothing; Boolean.true for permission allow, Boolean.false for permission deny 
	 */
	public GeneralSelectionType checkPermissionForEntityTemplateWhenEntityPermissionSetup(Permission.Type permissionType, EntityDetail entityDetail, Permission permission);
	
	/**
	 * check the descriptions for checkPermissionForEntity
	 */
	public GeneralSelectionType checkPermissionForPageDefaultModulesWhenPagePermissionSetup(Permission.Type permissionType, PageDetail pageDetail, Permission permission);
	
	public List<Permission> findAccountPermissions(Long accountId);
	
	/**
	 * @param path includes all the parents' uuid
	 * @param permissionDto
	 * @return
	 */
	public Map<Permission.Type, PermissionedStuff> findClosestParentNodePermissionedStuff(String path, Permission permissionDto);
	public Map<Permission.Type, PermissionedStuff> findCurrentNodePermissionedStuff(String nodeUuid, Permission permissionDto);

	public List<Permission> findGroupPermissions(Long groupId);
	
	/**
	 * @param mergedPermission this is mergedPermission for account or group
	 * @param folderUuid folder for product list
	 * @return {@link ApiResponse} null: no sql segment, and don't know preview permission for folder. <br/>
	 * 		apiresponse.success is true: folder preview permission is allowed. sql segment is generated in apiresponse.response1 (null or has segment).<br/>
	 *		apiresponse.success is false: folder preview permission is denied. sql segment is generated in apiresponse.response1 (null or has segment).<br/>
	 */
	public ApiResponse generateSqlConditionSegmentForProductList(Permission mergedPermission, String folderUuid);
	
	/**
	 * this method used for getting more information when stuffs update for entity and page, For example, module allow to copy or not if you give entity copy permission.<br/>
	 * 
	 * <strong>this method will be used with updatePermissionStuff method in most cases.</strong>
	 * 
	 * @param permissionUuid
	 * @param stuffUuid
	 * @param permissionType
	 * @param permissionValue
	 * @return
	 */
	public ApiResponse getExtraInfoForPermissionStuffUpdate(String permissionUuid, String stuffUuid, Permission.Type permissionType);
	
	public Map<Permission.Type, PermissionedStuff> getFullPermissionForModule(Long orgId);
	
	public Map<Permission.Type, PermissionedStuff> getFullPermissionForProduct(Long orgId);
	
	public Permission getMergedPermissionForAccount(Long accountId, boolean permissionIsUsingCheck);
	
	public Permission getMergedPermissionForGroups(boolean permissionIsUsingCheck, Long... groupids);
	
	public Permission getPermissionByUuid(String permissionUuid);
	
	public Permission getPermissionDto(String permissionUuid);
	
	/**
	 * get Boolean value for previewAllow, readAllow, copyAllow, modifyAllow. <br/>
	 * <strong>Note:</strong><br/>
	 * the values get here are independent values, for example readAllow value is not related to previewAllow value. 
	 * 
	 * @param currentNodePermissionedStuffs
	 * @param closestParentNodePermissionedStuffs
	 * @return
	 */
	public Map<Permission.Type, Boolean> getPermissionTypesValueByCurrentAndClosestParentPermissionedStuffs(
			Map<Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Type, PermissionedStuff> closestParentNodePermissionedStuffs);
	
	/**
	 * get Boolean related value for previewAllow, readAllow, copyAllow, modifyAllow.<br/>
	 * <strong>Note:</strong><br/>
	 * the values get here are related values, for example readAllow could be false if previewAllow is false; lower lever permission false, high level permission false also. 
	 * 
	 * @param currentNodePermissionedStuffs
	 * @param closestParentNodePermissionedStuffs
	 * @return the four permission's types value, either true or false;
	 */
	public Map<Permission.Type, Boolean> getPermissionTypeRelatedValueByCurrentAndClosestParentPermissionedStuffs(
			Map<Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Type, PermissionedStuff> closestParentNodePermissionedStuffs);
	
	/**
	 * 
	 * determine if the node can be in the list based on permission <br/>
	 * Note: folder is always allowed in the list when preview permission is not false!!. ?????
	 * 
	 * @param currentNodePermissionedStuffs
	 * @param closestParentNodePermissionedStuffs
	 * @param treeNode
	 * @return 
	 */
	public boolean isNodeInListAllowed(boolean isFolder, Map<Permission.Type, PermissionedStuff> currentNodePermissionedStuffs, Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs);
	
	public boolean isPermissionAllowed(Long accountId, Permission.Type permissionType, String targetUuid);

	public boolean isPermissionAllowed(Permission mergedPermission, Permission.Type permissionType, String targetUuid);
	public boolean isPermissionAllowed(Permission mergedPermission, Permission.Type permissionType, String targetUuid, String path);
	
	public boolean isPermissionAllowedForGroup(Long groupId, Permission.Type permissionType, String targetUuid);
	
	public Long newPermission(Permission permission);
	
	public ApiResponse newPermissionNode(String target, String targetId, String newNodeName, String targetOrgUuid);
	
	/**
	 * @param holder keeps all merged permissions <br/>
	 * @param plus permission needs to be merged to holder <br/>
	 * @param permissionIsUsingCheck true: check whether or not the permission is in using.
	 * <strong>note:</strong><br/>
	 * holder's startDate is now, holder's endDate will use plus's endDate if (plus's endDate before holder's endDate) && (plus is a validated permission for merge) <br/>
	 * The reason for holder's endDate to use the closest endDate is for refresh the account permissions when it needs! For example: 
	 * accountDto.mergedPermission.enddate.before(now) -> recalculate the permissions for the account -> refresh the accountDto in the session.<br/>
	 * <strong>carefull:</strong><br/>
	 * this method will mainly be used on AccountDto, user should be very careful to avoid DB changes for account/group permissions if the method used on POJO objects!!! 
	 */
	public void permissionsMerge(Permission holder, Permission plus, boolean permissionIsUsingCheck);
	
	public ApiResponse orgCanJoinModify(String type, Long groupId, String targetOrgUuid, String valueName, String value);
	
	public void setFullPermissionForGroup(Long groupId);
	
	public OrgCanJoin toggleOrgJoinGroupAllowDeny(String type, String targetOrgUuid, Long groupId);
	
	/**
	 * @param permissionUuid
	 * @param stuffUuid : product's uuid or moduledetail's uuid
	 * @param permissionType
	 * @param permissionValue : checkboxYes, checkboxNo, checkboxEmpty, checkboxYesFollow, checkboxNoFollow
	 * @return Apiresponse.<br/><strong>Note:</strong> Apiresponse.response1 will include JSON-DATA when Apiresponse.success is true, and Permission.Type is "copy" for Product & Page. <br/>
	 *         the JSON-DATA is: list of subNode extra info and self extra info. The exta info likes :<br/>
	 *         could this product/page sharable for other organizations, even you say sharable? For example, product/page sharable, but module is not sharable.   
	 */
	public ApiResponse updatePermissionStuff(String permissionUuid, String stuffUuid, Permission.Type permissionType, String permissionValue);
	
	public ApiResponse updatePermissionValue(String permissionUuid, String valueName, String updateValue);
	
	/**
	 * @param <T>
	 * @param theClass
	 * @param treeNode
	 * @param permissionDto
	 * @param closestParentNodePermissionedStuffs this is the optional param, which can fasten the update process. The reason is that nodes under same folder will have same closestParentNodePermissionedStuffs
	 */
	public <T> void updateTreeNodeForPermission(Class<T> theClass, 
			TreeNode treeNode, 
			Permission permissionDto, 
			Map<Permission.Type, PermissionedStuff> closestParentNodePermissionedStuffs, 
			Map<Permission.Type, Boolean> selectablePermissionTypeMap);
	
}

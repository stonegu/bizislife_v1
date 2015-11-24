package com.bizislife.core.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.MediaTreeNode;
import com.bizislife.core.controller.helper.TreeHelp;
import com.bizislife.core.hibernate.dao.AccountDao;
import com.bizislife.core.hibernate.dao.GroupDao;
import com.bizislife.core.hibernate.dao.MediaDao;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.Accountgroup;
import com.bizislife.core.hibernate.pojo.EntityDetail;
import com.bizislife.core.hibernate.pojo.EntityTreeLevelView;
import com.bizislife.core.hibernate.pojo.MediaDetail;
import com.bizislife.core.hibernate.pojo.NodeDetail;
import com.bizislife.core.hibernate.pojo.Permission;
import com.bizislife.core.hibernate.pojo.MediaDetail.MediaType;
import com.bizislife.core.hibernate.pojo.MediaTreeLevelView;
import com.bizislife.core.view.dto.AccountDto;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class MediaServiceImpl implements MediaService{
	
	@Autowired
	AccountService accountService;

	@Autowired
	TreeService treeService;

	@Autowired
	PermissionService permissionService;
	
	@Autowired
	AccountDao accountDao;
	
	@Autowired
	GroupDao groupDao;
	
	@Autowired
	MediaDao mediaDao;

	@Override
	@Transactional
	public String newMediaNode(MediaType nodeType, String parentNodeUuid, String nodeName, String newNodeUuid) {
		if(accountService.getCurrentAccount()!=null && nodeType!=null && StringUtils.isNotBlank(parentNodeUuid) && StringUtils.isNotBlank(nodeName)){
			XStream stream = new XStream(new DomDriver());

			Date now = new Date();
			
			// get parent node detail
			MediaDetail parentMediaDetail = mediaDao.getMediaDetailByUuid(parentNodeUuid.trim());
			
			if(parentMediaDetail!=null){
				StringBuilder path = new StringBuilder();
				if(StringUtils.isNotBlank(parentMediaDetail.getPath())){
					path.append(parentMediaDetail.getPath()).append("/");
				}
				path.append(parentMediaDetail.getMediauuid());
				
				// new MediaDetail
				if(StringUtils.isBlank(newNodeUuid)){
					newNodeUuid = UUID.randomUUID().toString();
				}
				MediaDetail mediaDetail = new MediaDetail(null, 
						newNodeUuid, 
						nodeName.trim(), 
						nodeType.getCode(), 
						null, 
						parentNodeUuid, 
						path.toString(), 
						null, 
						now, 
						parentMediaDetail.getOrganization_id(), 
						accountService.getCurrentAccount().getId(), 
						null);
				
				// for entity level view
				// create a new entityTreeNode for level view
				MediaTreeNode node = new MediaTreeNode();
				node.setPrettyName(nodeName);
				node.setSystemName(newNodeUuid);
				
				// get EntityTreeLevelView by parent's uuid
				MediaTreeLevelView levelView = mediaDao.getMediaTreeLevelViewByParentUuid(parentNodeUuid);
				if(levelView==null){
					levelView = new MediaTreeLevelView(null, parentNodeUuid.trim(), null, now, parentMediaDetail.getOrganization_id(), accountService.getCurrentAccount().getId());
				}
				
				// update levelview's nodes
				if(StringUtils.isNotBlank(levelView.getNodes())){
					stream.alias("treeNode", MediaTreeNode.class);
					List<MediaTreeNode> nodes = (List<MediaTreeNode>)stream.fromXML(levelView.getNodes().trim());
					if(nodes==null){
						nodes = new ArrayList<MediaTreeNode>();
					}
					nodes.add(node);
					stream.processAnnotations(ArrayList.class);
					StringWriter sw = new StringWriter();
					stream.alias("treeNode", MediaTreeNode.class);
					stream.marshal(nodes, new CompactWriter(sw));
					levelView.setNodes(sw.toString());
				}else{
					List<MediaTreeNode> nodes = new ArrayList<MediaTreeNode>();
					nodes.add(node);
					stream.processAnnotations(ArrayList.class);
					StringWriter sw = new StringWriter();
					stream.alias("treeNode", MediaTreeNode.class);
					stream.marshal(nodes, new CompactWriter(sw));
					levelView.setNodes(sw.toString());
				}
				
				mediaDao.saveMediaDetail(mediaDetail);
				mediaDao.saveMediaTreeLevelView(levelView);
				
				return newNodeUuid;

			}
		}
		
		return null;
	}

	@Override
	@Transactional
	public ApiResponse updateMediaDetailByFieldnameValue(String mediaUuid, String updateValueName, String updateValue) {
		
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		MediaDetail detail = mediaDao.getMediaDetailByUuid(mediaUuid);
		if(detail!=null && StringUtils.isNotBlank(updateValueName)){
			
			// permissin check
			AccountDto loginAccount = accountService.getCurrentAccount();
			boolean isModifyPermissionAllowed = permissionService.isPermissionAllowed(loginAccount.getId(), Permission.Type.modify, mediaUuid);

			if(isModifyPermissionAllowed){
				
				boolean updated = false;
				
				if(updateValueName.equals("prettyname")){
					detail.setPrettyname(updateValue!=null?updateValue.trim():null);
					updated = true;
					
					// update prettyname in tree level view:
					MediaTreeLevelView view = mediaDao.getMediaTreeLevelViewHasNode(detail.getMediauuid());
					if(view!=null){
						List<MediaTreeNode> nodes = TreeHelp.getTreeNodesFromXml(MediaTreeNode.class, view.getNodes());
						if(nodes!=null && nodes.size()>0){
							boolean viewUpdated = false;
							for(MediaTreeNode node : nodes){
								if(node.getSystemName().equals(detail.getMediauuid())){
									node.setPrettyName(detail.getPrettyname());
									viewUpdated = true;
									break;
								}
							}
							if(viewUpdated){
								String updatedxml = TreeHelp.getXmlFromTreeNodes(nodes);
								view.setNodes(updatedxml);
							}
						}
					}
				}
				
				if(updated){
					Long id = mediaDao.saveMediaDetail(detail);
					if(id!=null){
						apires.setSuccess(true);
						apires.setResponse1(updateValue);
					}
				}
				
			}else{
				apires.setResponse1("User "+loginAccount.getFirstname()+" doesn't have permission to modify media node: "+mediaUuid);
			}
			
		}else{
			apires.setResponse1("No enough information to process the update - mediauuid: "+mediaUuid+", updateValueName: "+updateValueName);
		}
		
		return apires;
	}

	@Override
	@Transactional(readOnly=true)
	public MediaDetail getMediaDetailByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return mediaDao.getMediaDetailByUuid(uuid);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<MediaDetail> findMediasInFolder(String targetUuid, MediaType mtype, String folderId) {
		
		Account targetAccount = accountDao.getAccountByUuid(targetUuid);
		Accountgroup targetGroup = groupDao.getGroupByUuid(targetUuid);
		
		if(targetAccount!=null || targetGroup!=null){
			
			Permission mergedPermission = null;
			if(targetAccount!=null){
				mergedPermission = permissionService.getMergedPermissionForAccount(targetAccount.getId(), true);
			}else if(targetGroup!=null){
				mergedPermission = permissionService.getMergedPermissionForGroups(true, new Long[]{targetGroup.getId()});
			}
			
			// only need to check folder permission
			boolean isPreviewPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.preview, folderId);
			if(isPreviewPermissionAllowed){
				List<NodeDetail> allMedias =  mediaDao.findMedialDetailsInFolder(folderId, true, true);
				if(allMedias!=null && allMedias.size()>0){
					List<MediaDetail> mediasToReturn = new ArrayList<MediaDetail>();
					for(NodeDetail detail : allMedias){
						
						if(mtype==null){
							if(!((MediaDetail)detail).getNodetype().equals(MediaType.folder.getCode())){
								mediasToReturn.add(((MediaDetail)detail));
							}
						}else{
							if(((MediaDetail)detail).getNodetype().equals(mtype.getCode())){
								mediasToReturn.add(((MediaDetail)detail));
							}
						}
					}
					
					return mediasToReturn;
				}
				
			}
			
		}
		
		return null;
	}

	@Override
	public List<MediaDetail> findMediasInOrg(String targetUuid, MediaType mtype, Long orgid) {
		
		Account targetAccount = accountDao.getAccountByUuid(targetUuid);
		Accountgroup targetGroup = groupDao.getGroupByUuid(targetUuid);
		
		if((targetAccount!=null || targetGroup!=null) && mtype!=null && orgid!=null){
			
			Permission mergedPermission = null;
			if(targetAccount!=null){
				mergedPermission = permissionService.getMergedPermissionForAccount(targetAccount.getId(), true);
			}else if(targetGroup!=null){
				mergedPermission = permissionService.getMergedPermissionForGroups(true, new Long[]{targetGroup.getId()});
			}
			
			// 1. find all medias with type first:
			List<NodeDetail> mediaDetails = mediaDao.findMediasWithTypeInOrg(mtype, orgid);
			if(mediaDetails!=null && mediaDetails.size()>0){
				// 2. get all direct folders from medias in 1.
				Map<String, List<MediaDetail>> folderWithMediasMap = new HashMap<String, List<MediaDetail>>();
				for(NodeDetail m: mediaDetails){
					if(folderWithMediasMap.get(m.getParentuuid())==null){
						List<MediaDetail> mds = new ArrayList<MediaDetail>();
						mds.add((MediaDetail)m);
						folderWithMediasMap.put(m.getParentuuid(), mds);
					}else{
						folderWithMediasMap.get(m.getParentuuid()).add((MediaDetail)m);
					}
				}
				// 3. permission check
				List<MediaDetail> returnMedias = new ArrayList<MediaDetail>();
				for(Map.Entry<String, List<MediaDetail>> entry : folderWithMediasMap.entrySet()){
					boolean isPreviewPermissionAllowed = permissionService.isPermissionAllowed(mergedPermission, Permission.Type.preview, entry.getKey());
					if(isPreviewPermissionAllowed){
						returnMedias.addAll(entry.getValue());
					}
				}
				if(returnMedias.size()>0) return returnMedias;
			}
		}
		return null;
	}


}

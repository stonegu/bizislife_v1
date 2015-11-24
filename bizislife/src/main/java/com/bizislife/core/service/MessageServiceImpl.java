package com.bizislife.core.service;

import java.io.StringWriter;
import java.util.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.hibernate.dao.*;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.hibernate.pojo.Message.MsgType;
import com.bizislife.core.hibernate.pojo.Topic.AccessLevel;
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.EntityTreeNode;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.controller.component.JsTreeNode;
import com.bizislife.core.controller.component.Pagination;
import com.bizislife.core.controller.component.PaginationNode;
import com.bizislife.core.controller.component.PaginationNode.PaginationNodeType;
import com.bizislife.core.controller.component.Privilege;
import com.bizislife.core.controller.component.TopicTreeNode;
import com.bizislife.core.view.dto.AccountDto;
import com.bizislife.util.definition.DatabaseRelatedCode;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.CompactWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;

@Service
public class MessageServiceImpl implements MessageService{

	private static final Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);
	
	@Autowired
	AccountDao accountDao;

	@Autowired
	OrganizationDao organizationDao;
	
	@Autowired
	GroupDao groupDao;
	
	@Autowired
	MessageDao messageDao;
	
    @Autowired
    private AccountService accountService;
	
	private String xmlToString(ActivityLogData data){
		if(data!=null){
			XStream stream = new XStream(new DomDriver());
			stream.processAnnotations(ActivityLogData.class);
			StringWriter sw = new StringWriter();
			stream.marshal(data, new CompactWriter(sw));
			return sw.toString();
		}
		return null;
	}

	@Override
	@Transactional
	public Long newActivity(Long whoCreate, Long belongToOrg, ActivityType type, ActivityLogData data) {
		if(whoCreate!=null && belongToOrg!=null && type!=null && data!=null){
			
			String dataString = xmlToString(data);
			
			Date now = new Date();
			ActivityLog activityLog = new ActivityLog(null,
					whoCreate,
					belongToOrg,
					type.name(),
					dataString,
					now
					);
			
			return messageDao.saveActivityLog(activityLog);
		}
		return null;
		
	}

	@Override
	@Transactional
	public Long newTopic(Topic topic) {
		if(topic!=null && StringUtils.isNotBlank(topic.getTopicroute()) && topic.getTopictype()!=null){
			Organization bizOrg = accountService.getBizOrg();
			// check if same topic already exist: check the topic route
			Topic topicInDb = messageDao.getTopicByTopicroute(topic.getTopicroute());
			if(topicInDb==null){
				AccountDto currentAccount = accountService.getCurrentAccount();
				// save topic
				if(topic.getAccesslevel()==null) topic.setAccesslevel(Topic.AccessLevel.privateTopic.getCode());
				if(topic.getCreator_id()==null){
					topic.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
				}
				if(topic.getOrg_id()==null){
					topic.setOrg_id(bizOrg.getId());
				}
				if(topic.getCreatedate()==null) topic.setCreatedate(new Date());
				return messageDao.saveTopic(topic);
				
			}else
				return topicInDb.getId();
		}
		
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public String newsLineGenerator(ActivityType activityType, ActivityLogData activityLogData){
		
		if(activityLogData!=null && activityLogData.getDataMap()!=null){
			if(activityType.equals(ActivityType.newProduct) || activityType.equals(ActivityType.cloneProduct)){
				Map<String, Object> dataMap = activityLogData.getDataMap();
				
				StringBuilder newsline = new StringBuilder();
				newsline.append(dataMap.get("operatorName"))
				.append(" inputs a new product (<a href=\"/jspPreview?type=instance&objuuid=")
				.append(dataMap.get("newProductUuid")).append("\" class=\"ajaxProductPreview\">")
				.append(dataMap.get("newProductName")).append("</a>) information in ")
				.append(dataMap.get("orgName"));
				
				return newsline.toString();
				
			}else if(activityType.equals(ActivityType.announceProduct)){
				Map<String, Object> dataMap = activityLogData.getDataMap();
				
				StringBuilder newsline = new StringBuilder();
				newsline.append(dataMap.get("operatorName"))
				.append(" introduces a new product (<a href=\"/jspPreview?type=instance&objuuid=")
				.append(dataMap.get("productUuid")).append("\" class=\"ajaxProductPreview\">")
				.append(dataMap.get("productName")).append("</a>) in ")
				.append(dataMap.get("orgName"));
				
				return newsline.toString();
				
			}
			else{
				return activityLogData.getDesc();
			}
			
		}
		
		
		return null;
	}

	
	@Override
	@Transactional(readOnly=true)
	public String newsLineGenerator(ActivityLog activityLog){
		if(activityLog!=null) {
			ActivityLogData activityLogData = activityLog.getActivityLogData();
			
			return newsLineGenerator(activityLog.getType(), activityLogData);
		}
		
		return null;
	}

	@Override
	@Transactional
	public void postFeed(Long topicId, Long activityLogId) {
		// get topic by id;
		Topic topic = getTopicById(topicId);
		// get ActivityLog from id
		ActivityLog activityLog = getActivityLogById(activityLogId);
//		ActivityLogData activityLogData = activityLog.getActivityLogData();
		
		// news line generate
		String newsLine = newsLineGenerator(activityLog);
		
		if(topic!=null && activityLog!=null){
			Date now = new Date();
			// find all subscribed accounts by the topic
			List<Account> subscribers = findSubscribersByTopic(topic);
			if(subscribers!=null && subscribers.size()>0){
				List<Message> msgs = new ArrayList<Message>();
				
				for(Account a : subscribers){
					Message message = new Message(null,
							a.getId(),
							a.getAccountuuid(),
							a.getOrganization_id(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							DatabaseRelatedCode.AccountRelated.accountIdForSystem.getDesc(),
							newsLine,
							Message.MsgType.topicPost.getCode(),
							Message.MsgStatus.unread.getCode(),
							now,
							null,
							activityLog
					);
					msgs.add(message);
				}
				if(msgs.size()>0){
					messageDao.saveMessages(msgs);
				}
				
			}
		}
	}

	@Override
	@Transactional
	public void postFeed(Long topicId, Msgbody msgBody, ActivityType activityType, ActivityLogData activityLogData) {
		
		// get topic by id;
		Topic topic = getTopicById(topicId);
		
		// news line generate
		String newsLine = newsLineGenerator(activityType, activityLogData);
		
		if(topic!=null && msgBody!=null){
			Date now = new Date();
			// find all subscribed accounts by the topic
			List<Account> subscribers = findSubscribersByTopic(topic);
			if(subscribers!=null && subscribers.size()>0){
				
				// save msgbody if not saved
				Long msgBodyId = null;
				if(msgBody.getId()==null) {
					msgBodyId = messageDao.saveMessageBody(msgBody);
				}
				
				
				List<Message> msgs = new ArrayList<Message>();
				
				for(Account a : subscribers){
					Message message = new Message(null,
							a.getId(),
							a.getAccountuuid(),
							a.getOrganization_id(),
							Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
							DatabaseRelatedCode.AccountRelated.accountIdForSystem.getDesc(),
							newsLine,
							Message.MsgType.memberSend.getCode(),
							Message.MsgStatus.unread.getCode(),
							now,
							msgBody,
							null
					);
					msgs.add(message);
				}
				if(msgs.size()>0){
					messageDao.saveMessages(msgs);
				}
				
			}
		}
		
	}
	
	@Override
	@Transactional(readOnly=true)
	public Topic getTopicById(Long topicId) {
		if(topicId!=null){
			return messageDao.getTopicById(topicId);
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Account> findSubscribersByTopic(Topic topic) {
		if(topic!=null){
			// find subscribes by topic routekey
			List<Subscribe> scbs = findSubscribesByTopicUuid(topic.getTopicuuid());
			
			if(scbs!=null && scbs.size()>0){
				StringBuilder acctIds = new StringBuilder(); 
				int size = scbs.size();
				for(Subscribe s : scbs){
					size--;
					acctIds.append(s.getAccount_id());
					if(size>0) acctIds.append(",");
				}
				
				// find all accounts by acctIds
				return accountDao.findAccountsByIds(acctIds.toString());
				
			}
			
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Subscribe> findSubscribesByTopicUuid(String topicUuid) {
		if(StringUtils.isNotBlank(topicUuid)){
			return messageDao.findSubscribesByTopicUuid(topicUuid);
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public ActivityLog getActivityLogById(Long logId) {
		if(logId!=null){
			return messageDao.getActivityLogById(logId);
		}
		return null;
	}

	@Override
	@Transactional
	public Map<String, List<String>> saveSubscribes(String[] topicUuids, Long accountId) {
		if(topicUuids!=null && topicUuids.length>0 && accountId!=null){
			Account account = accountDao.getAccountPojoById(accountId);
			if(account!=null){
				// get all account's subscribes
				List<Subscribe> existSubscribes = messageDao.findAccountSubscribes(account.getId());
				// put all subscribed topics into a array for compare
				Map<String, Subscribe> subscribedTopics = new HashMap<String, Subscribe>();
				if(existSubscribes!=null && existSubscribes.size()>0){
					for(Subscribe s : existSubscribes){
						subscribedTopics.put(s.getTopicuuid(), s);
					}
				}
				
				// this is the list of new subscribes needed to be saved
				List<Subscribe> newSubscribes = new ArrayList<Subscribe>();
				
				if(subscribedTopics!=null && subscribedTopics.size()>0){ // do compare
					for(String t : topicUuids){
						if(subscribedTopics.get(t)==null){
							// create a new subscribe and put into newSubscribes list
							newSubscribes.add(new Subscribe(null, t, accountId));
						}else{
							subscribedTopics.put(t, null);
						}
					}
					
				}else{ // topicRoutes a new subscribe
					for(String t : topicUuids){
						newSubscribes.add(new Subscribe(null, t, accountId));
					}
				}
				
				// save new
				List<String> savedTopicUuids = new ArrayList<String>();
				if(newSubscribes.size()>0){
					messageDao.saveAllSubscribes(newSubscribes);
					for(Subscribe s : newSubscribes){
						savedTopicUuids.add(s.getTopicuuid());
					}
				}
				
				// remove old
				List<Subscribe> delSubscribes = new ArrayList<Subscribe>();
				List<String> deletedTopicUuids = new ArrayList<String>();
				for(Map.Entry<String, Subscribe> e : subscribedTopics.entrySet()){
					if(e.getValue()!=null){
						delSubscribes.add(e.getValue());
						deletedTopicUuids.add(e.getKey());
					}
				}
				messageDao.delAllSubscribes(delSubscribes);
				
				Map<String, List<String>> resMap = new HashMap<String, List<String>>();
				resMap.put("saved", savedTopicUuids);
				resMap.put("deleted", deletedTopicUuids);
				return resMap;
				
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Subscribe> findAccountSubscribes(Long accountId) {
		if(accountId!=null) return messageDao.findAccountSubscribes(accountId);
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<Message> findMsgsByAccount(Long accountId, Message.MsgType type, int totalResultsNum, int offset) {
		if(accountId!=null){
			return messageDao.findMsgsByAccount(accountId, type, totalResultsNum, offset);
		}
		return null;
	}

	@Override
	@Transactional
	public Long newTopicWithAncestor(Topic topic) {

		if(topic!=null && StringUtils.isNotBlank(topic.getTopicroute()) && topic.getTopictype()!=null){
			Date now = new Date();
			
			Organization bizOrg = accountService.getBizOrg();
			Organization topicOrg = topic.getOrg_id()!=null?accountService.getOrgById(topic.getOrg_id()):bizOrg;
			
			String[] topicLeaf = topic.getTopicroute().split("\\.");
			// the array contains all topicroutes. if you give "sys.org.create", the array will contain "sys", "sys.org", "sys.org.create" 
			String[] topicRoutes = new String[topicLeaf.length];
			StringBuilder tempRoute = new StringBuilder();
			for(int i=0; i<topicLeaf.length; i++){
				if(i>0) tempRoute.append(".");
				tempRoute.append(topicLeaf[i]);
				topicRoutes[i] = tempRoute.toString();
			}
			
			// get all org's systemNames to put into the Set
			Map<String, Long> orgSysNameWithIds = new HashMap<String, Long>();
			List<Organization> orgs = organizationDao.findAllOrgainzations();
			if(orgs!=null){
				for(Organization o : orgs){
					orgSysNameWithIds.put(o.getOrgsysname(), o.getId());
				}
			}
			

			Topic tHub = null;
			Map<String, String> topicSystemNameMap = new HashMap<String, String>(); // to hold topic's route with it's system name for tree generation purpose.
			for(String route : topicRoutes){
				tHub = messageDao.getTopicByTopicroute(route);
				if(tHub!=null){
					if(tHub.getTopicroute().equals(topic.getTopicroute())){
						topic.setId(tHub.getId());
					}
					topicSystemNameMap.put(route, tHub.getTopicuuid());
				}else if(tHub==null){
					if(route.equals(topic.getTopicroute())){
						topicSystemNameMap.put(route, topic.getTopicuuid());
						if(topic.getAccesslevel()==null) topic.setAccesslevel(Topic.AccessLevel.privateTopic.getCode());
						if(topic.getCreator_id()==null){
							topic.setCreator_id(Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()));
						}
						if(topic.getOrg_id()==null){
							topic.setOrg_id(topicOrg.getId());
						}
						if(topic.getCreatedate()==null) topic.setCreatedate(now);
						messageDao.saveTopic(topic);
//						newTopics.add(topic);
					}else{
						// create a new topic for topic's ancestors
						Long topicBelongToOrg = bizOrg.getId(); // default topic belong to biz org
						for(Map.Entry<String, Long> e : orgSysNameWithIds.entrySet()){ // loop through all org's sysnames to find topic's belonging.
							if(route.indexOf(e.getKey())>-1){
								topicBelongToOrg = e.getValue();
								break;
							}
						}
						
						Topic ancestor = new Topic(null,
								UUID.randomUUID().toString(),
								route,
								route,
								Topic.AccessLevel.privateTopic.getCode(),
								Long.valueOf(DatabaseRelatedCode.AccountRelated.accountIdForSystem.getCode()),
								topicBelongToOrg,
								route,
								now,
								null,
								Topic.TopicType.SystemTopic.getCode(),
								null,
								null,
								null
								);
						topicSystemNameMap.put(route, ancestor.getTopicuuid());
						messageDao.saveTopic(ancestor);
					}
				}
			}
			
			// create tree and nodes for the topic
//			List<TopicTreeLevelView> modifiedTopicTreeLevelViews = new ArrayList<TopicTreeLevelView>(); // hold a list of modified treelevelviews for later update
			XStream stream = new XStream(new DomDriver());
			stream.alias("treeNode", TopicTreeNode.class);
			stream.processAnnotations(ArrayList.class);
			for(String route : topicRoutes){ // assume topicRoutes is sorted by length, like: sys, sys.org, sys.org.create, ...
				// get parent uuid, for example, sys.org's parent will be sys, but sys's parent will be null
				if(route.indexOf(".")>-1){ // has parent
					// check exist or not
					boolean exist = false;
					String routeSysName = topicSystemNameMap.get(route);
					if(StringUtils.isNotBlank(routeSysName)){
						TopicTreeLevelView topicTreeLevelViewHasTheTopic = messageDao.getTopicTreeLevelViewByTopicSysName(routeSysName);
						if(topicTreeLevelViewHasTheTopic!=null){
							exist = true;
						}
								
						if(!exist){ // create a new topic tree node and put into levelview
							String topicTreeNodePrettyName = route.substring(route.lastIndexOf(".")+1);
							String topicParentSysName = topicSystemNameMap.get(route.substring(0, route.lastIndexOf(".")));
							if(StringUtils.isNotBlank(topicParentSysName)){ // must have parent
								TopicTreeLevelView currentTopicTreeLevelView = messageDao.getTopicTreeLevelViewByParentId(topicParentSysName);
								// create a new node
								Long topicBelongToOrg = bizOrg.getId(); // default topic belong to biz org
								for(Map.Entry<String, Long> e : orgSysNameWithIds.entrySet()){ // loop through all org's sysnames to find topic's belonging.
									if(route.indexOf(e.getKey())>-1){
										topicBelongToOrg = e.getValue();
										break;
									}
								}
								TopicTreeNode newTn = new TopicTreeNode();
								newTn.setPrettyName(topicTreeNodePrettyName);
								newTn.setPrivilege(new Privilege(topicBelongToOrg, null, null));
								newTn.setSystemName(routeSysName);
								
								if(currentTopicTreeLevelView!=null){
									List<TopicTreeNode> tns = null;
									if(currentTopicTreeLevelView.getNodes()!=null){
										tns = (List<TopicTreeNode>)stream.fromXML(currentTopicTreeLevelView.getNodes());
									}
									if(tns==null){
										tns = new ArrayList<TopicTreeNode>();
									}
									tns.add(newTn);
									currentTopicTreeLevelView.setNodes(stream.toXML(tns));
									
									// update treelevelView
									messageDao.saveTopicTreeLevelView(currentTopicTreeLevelView);
									
								}else{ // create new levelView
									List<TopicTreeNode> tns = new ArrayList<TopicTreeNode>();
									tns.add(newTn);
									currentTopicTreeLevelView = new TopicTreeLevelView();
									currentTopicTreeLevelView.setCreatedate(now);
									currentTopicTreeLevelView.setNodes(stream.toXML(tns));
									currentTopicTreeLevelView.setParentuuid(topicParentSysName);
									messageDao.saveTopicTreeLevelView(currentTopicTreeLevelView);
									
								}
							}
						}
					}
					
				}else{ // without parent, the root
					// get all root(s) to check exist or not
					TopicTreeLevelView rootview = messageDao.getRootTopicTreeLevelView();
					// check exist or not
					boolean exist = false;
					List<TopicTreeNode> rts = null;
					if(rootview!=null){
						if(rootview.getNodes()!=null){
							rts = (List<TopicTreeNode>)stream.fromXML(rootview.getNodes());
							if(rts!=null && rts.size()>0){
								for(TopicTreeNode n : rts){
									if(n.getSystemName().equals(topicSystemNameMap.get(route))){
										exist = true;
										break;
									}
								}
							}
						}
					}
					
					// create a new view if root not exist
					if(!exist){
						// use rootview if rootviews!=null
						if(rootview!=null){
							TopicTreeNode newRt = new TopicTreeNode();
							newRt.setPrettyName(route);
							newRt.setPrivilege(new Privilege(bizOrg.getId(), null, null));// root will always belongs to biz org
							newRt.setSystemName(topicSystemNameMap.get(route));
							if(rts==null){
								rts = new ArrayList<TopicTreeNode>();
							}
							rts.add(newRt);

							// put new roots into view
							rootview.setNodes(stream.toXML(rts));
							messageDao.saveTopicTreeLevelView(rootview);
							
						}else{ // create a new root view
							TopicTreeNode newRt = new TopicTreeNode();
							newRt.setPrettyName(route);
							newRt.setPrivilege(new Privilege(bizOrg.getId(), null, null));// root will always belongs to biz org
							newRt.setSystemName(topicSystemNameMap.get(route));
							if(rts==null){
								rts = new ArrayList<TopicTreeNode>();
							}
							rts.add(newRt);
							rootview = new TopicTreeLevelView();
							rootview.setNodes(stream.toXML(rts));
							rootview.setCreatedate(now);
							messageDao.saveTopicTreeLevelView(rootview);
							
						}
						
					}
					
					
				}
				
			}
		}
		
		return topic.getId();
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public void applySubscribable(JsTreeNode node) {
		AccountDto currentAccount = accountService.getCurrentAccount();
		if(currentAccount!=null && node!=null){
			// find all TopicVisibleOrg, TopicVisibleGroup, TopicVisibleAccount related to current Account
			Long aOrgId = currentAccount.getOrganization_id();
			List<TopicVisibleOrg> topicVisibleOrgs = messageDao.findTopicVisibleOrgByOrgId(aOrgId);
			
			List<Long> aGroupIds = new ArrayList<Long>();
			if(currentAccount.getAccountGroups()!=null && currentAccount.getAccountGroups().size()>0){
				for(Accountgroup g : currentAccount.getAccountGroups()){
					aGroupIds.add(g.getId());
				}
			}
			List<TopicVisibleGroup> topicVisibleGroups = new ArrayList<TopicVisibleGroup>();
			List<TopicVisibleGroup> topicVisibleGroups_temp = null;
			if(aGroupIds.size()>0){
				for(Long gid : aGroupIds){
					topicVisibleGroups_temp = messageDao.findTopicVisibleGroupByGroupId(gid);
					if(topicVisibleGroups_temp!=null && topicVisibleGroups_temp.size()>0){
						topicVisibleGroups.addAll(topicVisibleGroups_temp);
					}
				}
			}

			Long accountId = currentAccount.getId();
			List<TopicVisibleAccount> topicVisibleAccounts = messageDao.findTopicVisibleAccountByAcctId(accountId);
			
			// find all topicUuids which list in topicVisibleOrgs, topicVisibleGroups, topicVisibleAccounts
			Set<String> subscribableTopicuuids = new HashSet<String>();
			if(topicVisibleOrgs!=null && topicVisibleOrgs.size()>0){
				for(TopicVisibleOrg t : topicVisibleOrgs){
					subscribableTopicuuids.add(t.getTopicuuid());
				}
			}
			if(topicVisibleGroups.size()>0){
				for(TopicVisibleGroup t : topicVisibleGroups){
					subscribableTopicuuids.add(t.getTopicuuid());
				}
			}
			if(topicVisibleAccounts!=null && topicVisibleAccounts.size()>0){
				for(TopicVisibleAccount t : topicVisibleAccounts){
					subscribableTopicuuids.add(t.getTopicuuid());
				}
			}
			
			// get all public topics information:
			List<Topic> allPublicTopics = messageDao.findAllPublicTopics();
			List<String> allPublicTopicUuids = new ArrayList<String>();
			if(allPublicTopics!=null && allPublicTopics.size()>0){
				for(Topic t : allPublicTopics){
					allPublicTopicUuids.add(t.getTopicuuid());
				}
			}
			
			// apply subscribable based on allPublicTopicUuids & subscribableTopicuuids for private topics
			List<JsTreeNode> nodes = new ArrayList<JsTreeNode>();
			node.flattenMe(nodes);
			if(nodes.size()>0){
				for(JsTreeNode n : nodes){
					if(n.getAttr()!=null && n.getAttr().getId()!=null && !allPublicTopicUuids.contains(n.getAttr().getId()) && !subscribableTopicuuids.contains(n.getAttr().getId())){
						n.getAttr().setRel("noCheckable");
						
						if(n.getData()!=null && n.getData().getAttr()!=null)
							n.getData().getAttr().addCssClass("noCheckable");
					}
					
//					if(n.getAttr()!=null && n.getAttr().getId()!=null && subscribableTopicuuids.contains(n.getAttr().getId())){
//						n.getAttr().addRel("sbsable");
//					}
					
				}
			}
		}
	}

	@Override
	@Transactional(readOnly=true)
	public Topic getTopicByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			return messageDao.getTopicByUuid(uuid);
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<GeneralSelectionType> findTopicVisibleAccountsInOrg(String topicUuid, Long orgId) {
		if(orgId!=null && StringUtils.isNotBlank(topicUuid)){
			// find all org's accounts
			List<AccountDto> accts = accountDao.findAccountsInOrg(orgId);
			if(accts!=null && accts.size()>0){
				// find all topicVisibleAccounts
				List<TopicVisibleAccount> visibleAccounts = messageDao.findTopicVisibleAccounts(topicUuid);
				List<Long> visibleAcctIds = null;
				if(visibleAccounts!=null && visibleAccounts.size()>0){
					visibleAcctIds = new ArrayList<Long>(visibleAccounts.size());
					for(TopicVisibleAccount a : visibleAccounts){
						visibleAcctIds.add(a.getAccount_id());
					}
				}
				
				List<GeneralSelectionType> accounts = new ArrayList<GeneralSelectionType>(accts.size());
				for(AccountDto a : accts){
					accounts.add(
						new GeneralSelectionType(
							a.getId().toString(), 
							a.getLoginname(), 
							visibleAcctIds!=null && visibleAcctIds.contains(a.getId()))
					);
				}
				return accounts;
			}
		}
		
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<GeneralSelectionType> findTopicVisibleGroupsInOrg(String topicUuid, Long orgId) {
		if(orgId!=null && StringUtils.isNotBlank(topicUuid)){
			// find all org's group
			List<Accountgroup> gps = groupDao.findOrgGroups(orgId);
			if(gps!=null && gps.size()>0){
				// find all topicVisibleGroups
				List<TopicVisibleGroup> visibleGroups = messageDao.findTopicVisibleGroups(topicUuid);
				List<Long> visibleGroupIds = null;
				if(visibleGroups!=null && visibleGroups.size()>0){
					visibleGroupIds = new ArrayList<Long>(visibleGroups.size());
					for(TopicVisibleGroup g : visibleGroups){
						visibleGroupIds.add(g.getGroup_id());
					}
				}
				
				List<GeneralSelectionType> groups = new ArrayList<GeneralSelectionType>(gps.size());
				for(Accountgroup g : gps){
					groups.add(
						new GeneralSelectionType(
							g.getId().toString(), 
							g.getGroupname(),
							visibleGroupIds!=null && visibleGroupIds.contains(g.getId()))
					);
				}
				return groups;
			}
		}
		return null;
	}

	@Override
	@Transactional(readOnly=true)
	public List<GeneralSelectionType> findTopicVisibleOrgs(String topicUuid) {
		if(StringUtils.isNotBlank(topicUuid)){
			Topic topic = messageDao.getTopicByUuid(topicUuid);
			
			if(topic!=null){
				
				List<Organization> orgs = organizationDao.findAllOrgainzations();
				if(orgs!=null && orgs.size()>0){
					// find all topicVisibleOrgs
					List<TopicVisibleOrg> visibleOrgs = messageDao.findTopicVisibleOrgs(topicUuid);
					List<Long> visibleOrgIds = null;
					if(visibleOrgs!=null && visibleOrgs.size()>0){
						visibleOrgIds = new ArrayList<Long>(visibleOrgs.size());
						for(TopicVisibleOrg o : visibleOrgs){
							visibleOrgIds.add(o.getOrganization_id());
						}
					}
					
					List<GeneralSelectionType> organizations = new ArrayList<GeneralSelectionType>(orgs.size());
					for(Organization o : orgs){
						if(o.getId().longValue()!=topic.getOrg_id().longValue()){
							organizations.add(
									new GeneralSelectionType(
										o.getId().toString(), 
										o.getOrgname(),
										visibleOrgIds!=null && visibleOrgIds.contains(o.getId())
									)
								);
							
						}
						
					}
					return organizations;
					
				}
				
			}
			
		}
		return null;
	}

	@Override
	@Transactional
	public void updateTopic(Topic topic) {
		if(topic!=null){
			messageDao.saveTopic(topic);
		}
	}

	@Override
	@Transactional
	public void updateTopicWithVisibles(String topicuuid,
			AccessLevel accessLevel, String[] visibleOrgs,
			String[] visibleGroups, String[] visibleAccounts) {

		if(StringUtils.isNotBlank(topicuuid)){
			// get topic by uuid
			Topic topic = messageDao.getTopicByUuid(topicuuid);
			if(accessLevel!=null && !accessLevel.equals(topic.getAccesslevel())) topic.setAccesslevel(accessLevel.getCode());
			
			// for visible things :)

			// the list need to remove from table
			List<TopicVisibleOrg> visibleOrgsNeedToRemove = new ArrayList<TopicVisibleOrg>();
			List<TopicVisibleGroup> visibleGroupsNeedToRemove = new ArrayList<TopicVisibleGroup>();
			List<TopicVisibleAccount> visibleAccountsNeedToRemove = new ArrayList<TopicVisibleAccount>();

			if(visibleOrgs!=null && visibleOrgs.length>0){
				if(topic.getVisibleToOrgs()!=null && topic.getVisibleToOrgs().size()>0){
					// the set need to add to topic
					Set<TopicVisibleOrg> vos = new HashSet<TopicVisibleOrg>();

					for(TopicVisibleOrg o : topic.getVisibleToOrgs()){
						if(ArrayUtils.contains(visibleOrgs, o.getOrganization_id().toString())){
							int index = ArrayUtils.indexOf(visibleOrgs, o.getOrganization_id().toString());
							visibleOrgs[index] = null; // set this to null to avoid duplicate TopicVisibleOrg in topic since this topic already visible to the org
						}else{
							visibleOrgsNeedToRemove.add(o); // this TopicVisibleOrg need to be removed from topic
						}
					}
					// fill vos set based on visibleOrgs array
					for(String o : visibleOrgs){
						if(NumberUtils.isDigits(o)){
							vos.add(new TopicVisibleOrg(null, topic.getId(), topic.getTopicuuid(), Long.valueOf(o)));
						}
					}
					
					// remove from the topic
					if(visibleOrgsNeedToRemove.size()>0){
						for(TopicVisibleOrg o : visibleOrgsNeedToRemove){
							topic.getVisibleToOrgs().remove(o);
						}
					}
					
					// add to topic
					topic.getVisibleToOrgs().addAll(vos);
				}else{ // add visible orgs only
					Set<TopicVisibleOrg> vos = new HashSet<TopicVisibleOrg>();
					for(String o : visibleOrgs){
						if(NumberUtils.isDigits(o)){
							vos.add(new TopicVisibleOrg(
									null, topic.getId(), topic.getTopicuuid(), Long.valueOf(o) 
									)
							);
						}
					}
					if(vos.size()>0){
						topic.removeAllVisibleToOrgs();
						for(TopicVisibleOrg to : vos){
							topic.addVisibleToOrg(to);
						}
					}
//						topic.setVisibleToOrgs(vos);
				}
			}else{ // remove all if topic has visible
				if(topic.getVisibleToOrgs()!=null && topic.getVisibleToOrgs().size()>0){
					for(TopicVisibleOrg o : topic.getVisibleToOrgs()){
						visibleOrgsNeedToRemove.add(o);
					}
					//topic.setVisibleToOrgs(null);
					topic.removeAllVisibleToOrgs();
				}
				
			}
			
			if(visibleGroups!=null && visibleGroups.length>0){
				if(topic.getVisibleToGroups()!=null && topic.getVisibleToGroups().size()>0){
					// the set need to add to topic
					Set<TopicVisibleGroup> vgs = new HashSet<TopicVisibleGroup>();

					for(TopicVisibleGroup o : topic.getVisibleToGroups()){
						if(ArrayUtils.contains(visibleGroups, o.getGroup_id().toString())){
							int index = ArrayUtils.indexOf(visibleGroups, o.getGroup_id().toString());
							visibleGroups[index] = null; // set this to null to avoid duplicate TopicVisibleGroup in topic since this topic already visible to the group
						}else{
							visibleGroupsNeedToRemove.add(o); // this TopicVisibleGroup need to be removed from topic
						}
					}
					// fill vos set based on visibleGroups array
					for(String o : visibleGroups){
						if(NumberUtils.isDigits(o)){
							vgs.add(new TopicVisibleGroup(null, topic.getId(), topic.getTopicuuid(), Long.valueOf(o)));
						}
					}
					
					// remove from the topic
					if(visibleGroupsNeedToRemove.size()>0){
						for(TopicVisibleGroup o : visibleGroupsNeedToRemove){
							topic.getVisibleToGroups().remove(o);
						}
					}
					
					// add to topic
					topic.getVisibleToGroups().addAll(vgs);
				}else{ // add visible groups only
					Set<TopicVisibleGroup> vgs = new HashSet<TopicVisibleGroup>();
					for(String o : visibleGroups){
						if(NumberUtils.isDigits(o)){
							vgs.add(new TopicVisibleGroup(
									null, topic.getId(), topic.getTopicuuid(), Long.valueOf(o) 
									)
							);
						}
					}
					if(vgs.size()>0){
//						topic.setVisibleToGroups(vgs);
						topic.removeAllVisibleToGroup();
						for(TopicVisibleGroup tg : vgs){
							topic.addVisibleToGroup(tg);
						}
					}
				}
				
			}else{ // remove all if topic has visible
				if(topic.getVisibleToGroups()!=null && topic.getVisibleToGroups().size()>0){
					for(TopicVisibleGroup o : topic.getVisibleToGroups()){
						visibleGroupsNeedToRemove.add(o);
					}
//					topic.setVisibleToGroups(null);
					topic.removeAllVisibleToGroup();
				}
				
			}
			
			if(visibleAccounts!=null && visibleAccounts.length>0){
				if(topic.getVisibleToAccounts()!=null && topic.getVisibleToAccounts().size()>0){
					// the set need to add to topic
					Set<TopicVisibleAccount> vas = new HashSet<TopicVisibleAccount>();

					for(TopicVisibleAccount o : topic.getVisibleToAccounts()){
						if(ArrayUtils.contains(visibleAccounts, o.getAccount_id().toString())){
							int index = ArrayUtils.indexOf(visibleAccounts, o.getAccount_id().toString());
							visibleAccounts[index] = null; // set this to null to avoid duplicate TopicVisibleAccount in topic since this topic already visible to the Account
						}else{
							visibleAccountsNeedToRemove.add(o); // this TopicVisibleAccount need to be removed from topic
						}
					}
					// fill vos set based on visibleAccounts array
					for(String o : visibleAccounts){
						if(NumberUtils.isDigits(o)){
							vas.add(new TopicVisibleAccount(null, topic.getId(), topic.getTopicuuid(), Long.valueOf(o)));
						}
					}
					
					// remove from the topic
					if(visibleAccountsNeedToRemove.size()>0){
						for(TopicVisibleAccount o : visibleAccountsNeedToRemove){
							topic.getVisibleToAccounts().remove(o);
						}
					}
					
					// add to topic
					topic.getVisibleToAccounts().addAll(vas);
				}else{ // add visible Accounts only
					Set<TopicVisibleAccount> vas = new HashSet<TopicVisibleAccount>();
					for(String o : visibleAccounts){
						if(NumberUtils.isDigits(o)){
							vas.add(new TopicVisibleAccount(
									null, topic.getId(), topic.getTopicuuid(), Long.valueOf(o) 
									)
							);
						}
					}
					if(vas.size()>0){
//						topic.setVisibleToAccounts(vas);
						topic.removeAllVisibleToAccount();
						for(TopicVisibleAccount ta : vas){
							topic.addVisibleToAccount(ta);
						}
					}
				}
				
			}else{ // remove all if topic has visible
				if(topic.getVisibleToAccounts()!=null && topic.getVisibleToAccounts().size()>0){
					for(TopicVisibleAccount o : topic.getVisibleToAccounts()){
						visibleAccountsNeedToRemove.add(o);
					}
//					topic.setVisibleToAccounts(null);
					topic.removeAllVisibleToAccount();
				}
				
			}
			
			// update topic
			messageDao.saveTopic(topic);
			
			// remove from db for visibleOrgsNeedToRemove, visibleGroupsNeedToRemove, visibleAccountsNeedToRemove
			for(TopicVisibleOrg o : visibleOrgsNeedToRemove){
				messageDao.delTopicVisibleOrg(o);
			}
			for(TopicVisibleGroup g : visibleGroupsNeedToRemove){
				messageDao.delTopicVisibleGroup(g);
			}
			for(TopicVisibleAccount a : visibleAccountsNeedToRemove){
				messageDao.delTopicVisibleAccount(a);
			}
			
			
		}
		
	}

	@Override
	@Transactional
	public void delMsg(Long msgId) {
		AccountDto currentAccount = accountService.getCurrentAccount();
		if(currentAccount!=null && msgId!=null){
			Message msg = messageDao.getMsgById(msgId);
			if(msg!=null 
					&& (currentAccount.getId().equals(msg.getAccount_id()) // self msg 
					|| currentAccount.isSystemDefaultAccount() && (currentAccount.isBizAccount() || currentAccount.getOrganization_id().equals(msg.getOrg_id())))){
				messageDao.delMsg(msg);
			}
		}
	}

	@Override
	@Transactional
	public Long saveSubscribe(String topicUuid, String subscribeType, Long accountId) {
		Topic topic = messageDao.getTopicByUuid(topicUuid);
		AccountDto account = accountDao.getAccountById(accountId);
		Long subscribeId = null;
		if(topic!=null && account!=null){
			// find subscribe by topic uuid and accountid 
			Subscribe subscribe = messageDao.getSubscribesByTopicUuidAccountId(topicUuid, accountId);
			if(StringUtils.equals(subscribeType, Subscribe.type.subscribe.name())){
				if(subscribe==null){
					subscribe = new Subscribe(null, topicUuid, accountId);
					subscribeId = messageDao.saveSubscribe(subscribe);
				}
				
			}else if(StringUtils.equals(subscribeType, Subscribe.type.unsubscribe.name())){
				if(subscribe!=null){
					subscribeId = subscribe.getId();
					messageDao.delSubscribe(subscribe.getId());
				}
			}
			
		}
		
		
		return subscribeId;
	}

	@Override
	@Transactional(readOnly=true)
	public int countMsg(Long accountId, MsgType type) {
		return messageDao.countMsg(accountId, type);
	}

	@Override
	@Transactional(readOnly=true)
	public Pagination getPaginationForMessage(String hostname, Long accountId, MsgType msgtype, int totalResultsNumInPage, int pageIdx) {
		if(totalResultsNumInPage<=0) totalResultsNumInPage = Integer.MAX_VALUE;
		if(pageIdx<1) pageIdx = 1;
		
		if(msgtype==null) msgtype = MsgType.topicPost;
		String urlSegment = null;
		if(msgtype.equals(MsgType.topicPost)){
			urlSegment = "/news";
		}else if(msgtype.equals(MsgType.memberSend)){
			urlSegment = "/messages";
		}
		
		int totalmsg = countMsg(accountId, msgtype);
		
		// default show 10 pages: NOTE: this number must bigger than 2 (>2)!!!
		int DEFAULTNUMBEROFPAGE = 5;
		int totalNumberOfPagesToShow = DEFAULTNUMBEROFPAGE;

		if(totalmsg>totalResultsNumInPage){
			int numberOfPages = totalmsg / totalResultsNumInPage;
			int remainder = totalmsg % totalResultsNumInPage;
			if(remainder>0){
				numberOfPages++;
			}
			if(pageIdx>numberOfPages){
				pageIdx = numberOfPages;
			}
			
			Pagination pagination = new Pagination();
			
			// go to first
			StringBuilder goFirstUrl = new StringBuilder();
			goFirstUrl.append(urlSegment).append("?totalResultsNum=").append(totalResultsNumInPage).append("&pageIdx=1");
			PaginationNode goFirstPageNode = new PaginationNode("<<", goFirstUrl.toString(), "page 1", PaginationNodeType.goFirst, pageIdx==1);
			pagination.addPaginationNode(goFirstPageNode);
			
			// go to previous
			StringBuilder goPreviousUrl = new StringBuilder();
			goPreviousUrl.append(urlSegment).append("?totalResultsNum=").append(totalResultsNumInPage).append("&pageIdx=").append(pageIdx<=1?1:(pageIdx-1));
			PaginationNode goPreviousPageNode = new PaginationNode("<", goPreviousUrl.toString(), "page "+(pageIdx<=1?1:(pageIdx-1)), PaginationNodeType.goPrevious, pageIdx==(pageIdx<=1?1:(pageIdx-1)));
			pagination.addPaginationNode(goPreviousPageNode);
			
			// check the totalNumberOfPagesToShow again!!
			if(totalNumberOfPagesToShow>numberOfPages) totalNumberOfPagesToShow = numberOfPages;
			
			// check currentPageIdx again!!
			if(pageIdx>numberOfPages){
				pageIdx = numberOfPages;
			}
			
			// determine which position is the middle position
			int middlePagePosition = totalNumberOfPagesToShow/2 + 1;
			
			// page numbers to show is determined by currentPagePosition & pageIndex
			if(pageIdx<=middlePagePosition){ // display from 1 to numberofpages
				for(int i=1; i<=totalNumberOfPagesToShow; i++){
					StringBuilder url = new StringBuilder();
					url.append(urlSegment).append("?totalResultsNum=").append(totalResultsNumInPage).append("&pageIdx=").append(i);
					
					PaginationNode paginationNode = new PaginationNode(Integer.toString(i), url.toString(), "page "+i, PaginationNodeType.number, pageIdx==i);
					pagination.addPaginationNode(paginationNode);
				}
			}else{ // display from (currentPageIdx-totalNumberOfPagesToShow/2) to (currentPageIdx+totalNuberOfPagesToShow/2-1)
				
				int toIdx = pageIdx + middlePagePosition - 1;
				if(totalNumberOfPagesToShow%2==0){
					toIdx = toIdx - 1;
				}
				// in case that currentPage's position beyond middlePagePosition, ie: 1 2 3 [4 5 (6) 7] 8 : leftswitchNum=0 -> 1 2 3 4 [5 6 (7) 8] : leftswitchNum=0 -> 1 2 3 4 [5 6 7 (8)] : leftswitchNum=1 
				int leftswitchNum = (numberOfPages-toIdx>=0)?0:(toIdx-numberOfPages);
				toIdx = toIdx - leftswitchNum;
				
				int fromIdx = pageIdx - middlePagePosition + 1;
				fromIdx = fromIdx - leftswitchNum;
				
				for(int i=fromIdx; i<=toIdx; i++){
					// create url based on numberOfPages
					StringBuilder url = new StringBuilder();
					url.append(urlSegment).append("?totalResultsNum=").append(totalResultsNumInPage).append("&pageIdx=").append(i);
					PaginationNode paginationNode = new PaginationNode(Integer.toString(i), url.toString(), "page "+i, PaginationNodeType.number, pageIdx==i);
					pagination.addPaginationNode(paginationNode);
					
				}
			}

			// go to next
			StringBuilder goNextUrl = new StringBuilder();
			goNextUrl.append(urlSegment).append("?totalResultsNum=").append(totalResultsNumInPage).append("&pageIdx=").append(pageIdx>=numberOfPages?numberOfPages:(pageIdx+1));
			PaginationNode goNextPageNode = new PaginationNode(">", goNextUrl.toString(), "page "+(pageIdx>=numberOfPages?numberOfPages:(pageIdx+1)), PaginationNodeType.goNext, pageIdx==(pageIdx>=numberOfPages?numberOfPages:(pageIdx+1)));
			pagination.addPaginationNode(goNextPageNode);
			
			// go to last
			StringBuilder goLastUrl = new StringBuilder();
			goLastUrl.append(urlSegment).append("?totalResultsNum=").append(totalResultsNumInPage).append("&pageIdx=").append(numberOfPages);
			PaginationNode goLastPageNode = new PaginationNode(">>", goLastUrl.toString(), "page "+numberOfPages, PaginationNodeType.goLast, pageIdx==numberOfPages);
			pagination.addPaginationNode(goLastPageNode);

			// for extraInfo
			StringBuilder paginationExtraInfo = new StringBuilder("showing ");
			int fromNodeNumber = (pageIdx - 1) * totalResultsNumInPage + 1;
			int toNodeNumber = pageIdx * (totalResultsNumInPage==Integer.MAX_VALUE?totalmsg:totalResultsNumInPage);
			paginationExtraInfo.append(fromNodeNumber);
			if(toNodeNumber<=totalmsg){
				paginationExtraInfo.append("-").append(toNodeNumber);
			}
			paginationExtraInfo.append(" of ").append(totalmsg);
			pagination.setExtraInfo(paginationExtraInfo.toString());
			
			return pagination;
		}

		
		return null;
	}


}

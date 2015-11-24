package com.bizislife.core.service;

import java.util.*;

import com.bizislife.core.hibernate.pojo.ActivityLog.ActivityType;
import com.bizislife.core.hibernate.pojo.*;
import com.bizislife.core.controller.component.ActivityLogData;
import com.bizislife.core.controller.component.GeneralSelectionType;
import com.bizislife.core.controller.component.JsTreeNode;
import com.bizislife.core.controller.component.Pagination;

public interface MessageService {
	
	/**
	 * 
	 * apply subscribable to the node and its subnodes
	 * @param node
	 */
	public void applySubscribable(JsTreeNode node);
	
	public int countMsg(Long accountId, Message.MsgType type);
	
	public void delMsg(Long msgId);
	
	public List<Subscribe> findAccountSubscribes(Long accountId);
	
	public List<Message> findMsgsByAccount(Long accountId, Message.MsgType type, int totalResultsNum, int offset);
	
	public List<Account> findSubscribersByTopic(Topic topic);
	
	public List<Subscribe> findSubscribesByTopicUuid(String topicUuid);
	
	public List<GeneralSelectionType> findTopicVisibleAccountsInOrg(String topicUuid, Long orgId);
	public List<GeneralSelectionType> findTopicVisibleGroupsInOrg(String topicUuid, Long orgId);
	
	/**
	 * @param topicUuid
	 * @return orgs without topic's org
	 */
	public List<GeneralSelectionType> findTopicVisibleOrgs(String topicUuid);
	
	public ActivityLog getActivityLogById(Long logId);
	
	public Pagination getPaginationForMessage(String hostname, Long accountId, Message.MsgType msgtype, int totalResultsNumInPage, int pageIdx);
	
	public Topic getTopicById(Long topicId);
	
	public Topic getTopicByUuid(String uuid);
	
	/**
	 * @param whoCreate log creator's id, 0 for system creates the log
	 * @param belongToOrg the log belongs to
	 * @param type ActivityLog.ActivityType
	 * @param data xml string of ActivityLogData.java
	 * @return ActivityLog's id
	 */
	public Long newActivity(Long whoCreate, Long belongToOrg, ActivityType type, ActivityLogData data);
	
	
	public String newsLineGenerator(ActivityLog activityLog);
	
	public String newsLineGenerator(ActivityType activityType, ActivityLogData activityLogData);
	
	
	/**
	 * this method create a new topic based on Topic.topicroute. The step is:<br/>
	 * 1) check the topicroute is exist or not, the method will return exist topic id if topicroute is alread exist,
	 * 		otherwise, it will create a new topic <br/>
	 * 
	 * @param topic
	 * @return topic id
	 */
	public Long newTopic(Topic topic);
	
	/**
	 * this method create a new topic and it ancestors. For example, new "sys.org.creator" topic will create "sys", "sys.org", "sys.org.creator" 3 topics. <br/>
	 * The step is: <br/>
	 * 1) check the topic with it's ancestor are exist or not,<br/> 
	 * 2) create any topic and it's ancestor if any of them are not exist (topic table)<br/>
	 * 3) create node(s) in the tree <br/>
	 * 4) return topic id <br/>
	 * @param topic
	 * @return
	 */
	public Long newTopicWithAncestor(Topic topic);
	
	public void postFeed(Long topicId, Long activityLogId);
	public void postFeed(Long topicId, Msgbody msgBody, ActivityType activityType, ActivityLogData activityLogData);
	
	public Long saveSubscribe(String topicUuid, String subscribeType, Long accountId);
	
	/**
	 * Save new subscribes for accountId. Note: this method only used in form submit, not in ajax call to subscribe a topic!!!!!
	 * @param topicRoutes subscribed topicRoutes
	 * @param accountId
	 * @return map for saved list and deleted list
	 */
	public Map<String, List<String>> saveSubscribes(String[] topicUuids, Long accountId);
	
	public void updateTopic(Topic topic);
	
	public void updateTopicWithVisibles(String topicuuid, Topic.AccessLevel accessLevel, String[] visibleOrgs, String[] visibleGroups, String[] visibleAccounts);
	

}

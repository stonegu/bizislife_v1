package com.bizislife.core.hibernate.dao;

import com.bizislife.core.hibernate.pojo.*;

import java.util.*;


public interface MessageDao {
	
	public int countMsg(Long accountId, Message.MsgType type);
	
	public int countTotalSystemTopicUnderNode(String topicUuid);
	
	public void delAllSubscribes(List<Subscribe> subscribes);
	
	public void delMsg(Message msg);
	
	public void delSubscribe(Long subscribeid);
	
	public void delTopicVisibleAccount(TopicVisibleAccount target);
	public void delTopicVisibleGroup(TopicVisibleGroup target);
	public void delTopicVisibleOrg(TopicVisibleOrg target);
	
	public List<Subscribe> findAccountSubscribes(Long accountId);
	
	public List<Topic> findAllPublicTopics();
	
	public List<Topic> findAllTopics();
	
	public List<Message> findMsgsByAccount(Long accountId, Message.MsgType type, int totalResultsNum, int offset);
	
	/**
	 * @return roots for whole topic tree
	 */
	public TopicTreeLevelView getRootTopicTreeLevelView();
	
	public List<Subscribe> findSubscribesByTopicUuid(String topicUuid);
	
	public List<TopicVisibleAccount> findTopicVisibleAccountByAcctId(Long accountId);
	public List<TopicVisibleGroup> findTopicVisibleGroupByGroupId(Long groupId);
	public List<TopicVisibleOrg> findTopicVisibleOrgByOrgId(Long orgId);
	
	public List<TopicVisibleAccount> findTopicVisibleAccounts(String topicUuid);
	public List<TopicVisibleGroup> findTopicVisibleGroups(String topicUuid);
	public List<TopicVisibleOrg> findTopicVisibleOrgs(String topicUuid);
	
	public ActivityLog getActivityLogById(Long logId);
	
	public Message getMsgById(Long msgId);
	
	public Subscribe getSubscribesByTopicUuidAccountId(String topicUuid, Long accountId);
	
	public Topic getTopicById(Long topicId);
	
	/**
	 * @param topicroute
	 * @return
	 */
	public Topic getTopicByTopicroute(String topicroute);

	public Topic getTopicByUuid(String uuid);
	
	public TopicTreeLevelView getTopicTreeLevelViewByParentId(String parentSysName);
	
	/**
	 * @param topicSysName
	 * @return topicTreeLevelView which include topicSysName
	 */
	public TopicTreeLevelView getTopicTreeLevelViewByTopicSysName(String topicSysName);
	
	/**
	 * @param cat
	 * @return
	 */
	public Tree getTreeByCategory(Tree.TreeCategory cat);
	
	
	/**
	 * save (no id) or update (has id).
	 * @param activityLog
	 * @return activityLog id
	 */
	public Long saveActivityLog(ActivityLog activityLog);
	
	public void saveAllSubscribes(List<Subscribe> subscribes);
	
	public Long saveMessageBody(Msgbody msgBody);
	
	public void saveMessages(List<Message> msgs);
	
	public Long saveSubscribe(Subscribe subscribe);

	/**
	 * save or update
	 * @param topic
	 * @return
	 */
	public Long saveTopic(Topic topic);
	
	public Long saveTopicTreeLevelView(TopicTreeLevelView treeLevelView);
	
	public Long saveTree(Tree tree);

}

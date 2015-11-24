package com.bizislife.core.hibernate.dao;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUtil;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bizislife.core.hibernate.pojo.ActivityLog;
import com.bizislife.core.hibernate.pojo.Message;
import com.bizislife.core.hibernate.pojo.Message.MsgType;
import com.bizislife.core.hibernate.pojo.Msgbody;
import com.bizislife.core.hibernate.pojo.Subscribe;
import com.bizislife.core.hibernate.pojo.Topic;
import com.bizislife.core.hibernate.pojo.TopicTreeLevelView;
import com.bizislife.core.hibernate.pojo.TopicVisibleAccount;
import com.bizislife.core.hibernate.pojo.TopicVisibleGroup;
import com.bizislife.core.hibernate.pojo.TopicVisibleOrg;
import com.bizislife.core.hibernate.pojo.Tree;
import com.bizislife.core.hibernate.pojo.Tree.TreeCategory;
import com.bizislife.util.BizHibernateTemplate;

@Repository("messageDao")
//@Transactional
public class MessageDaoImpl implements MessageDao {
    private static final Logger logger = LoggerFactory.getLogger(MessageDaoImpl.class);

    /*
    private BizHibernateTemplate hibernateTemplate;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new BizHibernateTemplate(sessionFactory);
    }
*/
    @Autowired
    DaoUtil daoUtil;
    
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Long saveActivityLog(ActivityLog activityLog) {
		if(activityLog!=null){
			if(activityLog.getId()!=null){
//				Long id = activityLog.getId();
//				hibernateTemplate.update(activityLog);
//				return id;
				return entityManager.merge(activityLog).getId();
			}else{
//				return (Long)hibernateTemplate.save(activityLog);
				entityManager.persist(activityLog);
				return activityLog.getId();
			}
		}
		
		return null;
	}

	@Override
	public Topic getTopicByTopicroute(String topicroute) {
		if(StringUtils.isNotBlank(topicroute)){
			StringBuilder q = new StringBuilder("FROM Topic where topicroute = '").append(topicroute).append("'");
//			List<Topic> topics = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Topic> topics = query.getResultList();
			if(topics!=null && topics.size()>0){
				return topics.get(0);
			}
		}
		
		return null;
	}

	@Override
	public Long saveTopic(Topic topic) {
		if(topic!=null && StringUtils.isNotBlank(topic.getTopicroute()) && topic.getTopictype()!=null){
			if(topic.getId()!=null){
//				hibernateTemplate.update(topic);
//				return topic.getId();
				entityManager.merge(topic).getId();
			}else{
//				return (Long)hibernateTemplate.save(topic);
				Set<TopicVisibleOrg> visibleOrgs = daoUtil.getRelationshipReferences(topic.getVisibleToOrgs());
				topic.removeAllVisibleToOrgs();
				if(visibleOrgs!=null && visibleOrgs.size()>0){
					for(TopicVisibleOrg to : visibleOrgs){
						topic.addVisibleToOrg(to);
					}
				}
				
				Set<TopicVisibleGroup> visibleGroups = daoUtil.getRelationshipReferences(topic.getVisibleToGroups());
				topic.removeAllVisibleToGroup();
				if(visibleGroups!=null && visibleGroups.size()>0){
					for(TopicVisibleGroup tg : visibleGroups){
						topic.addVisibleToGroup(tg);
					}
				}
				
				Set<TopicVisibleAccount> visibleAccounts = daoUtil.getRelationshipReferences(topic.getVisibleToAccounts());
				topic.removeAllVisibleToAccount();
				if(visibleAccounts!=null && visibleAccounts.size()>0){
					for(TopicVisibleAccount ta : visibleAccounts){
						topic.addVisibleToAccount(ta);
					}
				}
				
				entityManager.persist(topic);
				return topic.getId();
			}
		}
		
		return null;
	}

	@Override
	public Tree getTreeByCategory(TreeCategory cat) {
		if(cat!=null){
			StringBuilder q = new StringBuilder("FROM Tree where category = '").append(cat.name()).append("'");
//			List<Tree> trees = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Tree> trees = query.getResultList();
			if(trees!=null && trees.size()>0){
				return trees.get(0);
			}
		}
		return null;
	}

	@Override
	public Long saveTree(Tree tree) {
		if(tree!=null){
			if(tree.getId()!=null){
//				hibernateTemplate.update(tree);
//				return tree.getId();
				return entityManager.merge(tree).getId();
			}else{
//				return (Long)hibernateTemplate.save(tree);
				entityManager.persist(tree);
				return tree.getId();
			}
		}
		return null;
	}

	@Override
	public Topic getTopicById(Long topicId) {
//		return hibernateTemplate.get(Topic.class, topicId);
		return entityManager.find(Topic.class, topicId);
	}

	@Override
	public ActivityLog getActivityLogById(Long logId) {
		if(logId!=null){
//			return hibernateTemplate.get(ActivityLog.class, logId);
			return entityManager.find(ActivityLog.class, logId);
		}
		return null;
	}

	@Override
	public void saveMessages(List<Message> msgs) {
		if(msgs!=null && msgs.size()>0){
//			hibernateTemplate.saveOrUpdateAll(msgs);
			for(Message m : msgs){
				if(m.getId()!=null){
					entityManager.merge(m);
				}else{
					Msgbody mbody = (Msgbody)daoUtil.getRelationshipReference(m.getMsgbody());
					m.setMsgbody(mbody);
					
					ActivityLog activityLog = (ActivityLog)daoUtil.getRelationshipReference(m.getActivityLog());
					m.setActivityLog(activityLog);
					
					entityManager.persist(m);
				}
			}
		}
	}

	@Override
	public List<Subscribe> findAccountSubscribes(Long accountId) {
		if(accountId!=null){
			StringBuilder q = new StringBuilder("FROM Subscribe where account_id = ").append(accountId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public void saveAllSubscribes(List<Subscribe> subscribes) {
		if(subscribes!=null && subscribes.size()>0){
//			hibernateTemplate.saveOrUpdateAll(subscribes);
			for(Subscribe s : subscribes){
				if(s.getId()!=null){
					entityManager.merge(s);
				}else{
					entityManager.persist(s);
				}
			}
		}
		
	}

	@Override
	public void delAllSubscribes(List<Subscribe> subscribes) {
		if(subscribes!=null && subscribes.size()>0){
//			hibernateTemplate.deleteAll(subscribes);
			for(Subscribe s : subscribes){
				entityManager.remove(s);
			}
		}
		
	}

	@Override
	public List<Subscribe> findSubscribesByTopicUuid(String topicUuid) {
		if(StringUtils.isNotBlank(topicUuid)){
			StringBuilder q = new StringBuilder("FROM Subscribe where topicuuid = '").append(topicUuid.trim()).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<Message> findMsgsByAccount(Long accountId, Message.MsgType type, int totalResultsNum, int offset) {
		if(accountId!=null){
			StringBuilder q = new StringBuilder("FROM Message where account_id = ").append(accountId);
			
			if(type!=null){
				q.append(" and msgtype = '").append(type.getCode()).append("'");
			}
			
			// set order:
			q.append(" ORDER BY id DESC");
			
			Query query = entityManager.createQuery(q.toString());
			
			if(offset>=0){
				query.setFirstResult(offset);
			}
			if(totalResultsNum>0){
				query.setMaxResults(totalResultsNum);
			}
			
			return query.getResultList();
		}
		
		return null;
	}

	@Override
	public List<Topic> findAllTopics() {
		StringBuilder q = new StringBuilder("FROM Topic");
//		return hibernateTemplate.find(q.toString());
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}
	
	@Override
	public List<Topic> findAllPublicTopics(){
		StringBuilder q = new StringBuilder("FROM Topic where accesslevel = '").append(Topic.AccessLevel.publicTopic.getCode()).append("'");
//		return hibernateTemplate.find(q.toString());
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}

	@Override
	public List<TopicVisibleAccount> findTopicVisibleAccountByAcctId(Long accountId) {
		if(accountId!=null){
			StringBuilder q = new StringBuilder("FROM TopicVisibleAccount where account_id = ").append(accountId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();

		}
		return null;
	}

	@Override
	public List<TopicVisibleGroup> findTopicVisibleGroupByGroupId(Long groupId) {
		if(groupId!=null){
			StringBuilder q = new StringBuilder("FROM TopicVisibleGroup where group_id = ").append(groupId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();

		}
		return null;
	}

	@Override
	public List<TopicVisibleOrg> findTopicVisibleOrgByOrgId(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM TopicVisibleOrg where organization_id =").append(orgId);
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();

		}
		return null;
	}

	@Override
	public Topic getTopicByUuid(String uuid) {
		if(StringUtils.isNotBlank(uuid)){
			StringBuilder q = new StringBuilder("FROM Topic where topicuuid = '").append(uuid).append("'");
//			List<Topic> topics = hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			List<Topic> topics = query.getResultList();
			if(topics!=null && topics.size()>0) return topics.get(0);
		}
		
		return null;
	}

	@Override
	public List<TopicVisibleAccount> findTopicVisibleAccounts(String topicUuid) {
		if(StringUtils.isNotBlank(topicUuid)){
			StringBuilder q = new StringBuilder("FROM TopicVisibleAccount where topicuuid = '").append(topicUuid.trim()).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<TopicVisibleGroup> findTopicVisibleGroups(String topicUuid) {
		if(StringUtils.isNotBlank(topicUuid)){
			StringBuilder q = new StringBuilder("FROM TopicVisibleGroup where topicuuid = '").append(topicUuid.trim()).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<TopicVisibleOrg> findTopicVisibleOrgs(String topicUuid) {
		if(StringUtils.isNotBlank(topicUuid)){
			StringBuilder q = new StringBuilder("FROM TopicVisibleOrg where topicuuid = '").append(topicUuid.trim()).append("'");
//			return hibernateTemplate.find(q.toString());
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public void delTopicVisibleAccount(TopicVisibleAccount target) {
//		if(target!=null) hibernateTemplate.delete(target);
		if(target!=null){
			entityManager.remove(target);
		}
	}

	@Override
	public void delTopicVisibleGroup(TopicVisibleGroup target) {
//		if(target!=null) hibernateTemplate.delete(target);
		if(target!=null) entityManager.remove(target);
		
	}

	@Override
	public void delTopicVisibleOrg(TopicVisibleOrg target) {
//		if(target!=null) hibernateTemplate.delete(target);
		if(target!=null) entityManager.remove(target);
	}

	@Override
	public void delMsg(Message msg) {
		if(msg!=null){
//			hibernateTemplate.delete(msg);
			entityManager.remove(msg);
		}
	}

	@Override
	public Message getMsgById(Long msgId) {
		if(msgId!=null){
//			return hibernateTemplate.get(Message.class, msgId);
			return entityManager.find(Message.class, msgId);
		}
		return null;
	}

	@Override
	public TopicTreeLevelView getRootTopicTreeLevelView() {
		StringBuilder q = new StringBuilder("FROM TopicTreeLevelView where parentuuid is null");
		Query query = entityManager.createQuery(q.toString());
		List<TopicTreeLevelView> roots = query.getResultList();
		if(roots!=null && roots.size()>0) return roots.get(0);
		return null;
	}

	@Override
	public TopicTreeLevelView getTopicTreeLevelViewByTopicSysName(String topicSysName) {
		if(StringUtils.isNotBlank(topicSysName)){
			StringBuilder q = new StringBuilder("FROM TopicTreeLevelView WHERE nodes LIKE '%").append(topicSysName).append("%'");
			Query query = entityManager.createQuery(q.toString());
			List<TopicTreeLevelView> views = query.getResultList();
			if(views!=null && views.size()>0) return views.get(0);
		}
		return null;
	}

	@Override
	public Long saveTopicTreeLevelView(TopicTreeLevelView treeLevelView) {
		if(treeLevelView!=null){
			if(treeLevelView.getId()!=null){
				return entityManager.merge(treeLevelView).getId();
			}else{
				entityManager.persist(treeLevelView);
				return treeLevelView.getId();
			}
		}
		return null;
	}

	@Override
	public TopicTreeLevelView getTopicTreeLevelViewByParentId(String parentSysName) {
		StringBuilder q = new StringBuilder("FROM TopicTreeLevelView where parentuuid = '").append(parentSysName).append("'");
		Query query = entityManager.createQuery(q.toString());
		List<TopicTreeLevelView> views = query.getResultList();
		if(views!=null && views.size()>0) return views.get(0);
		return null;
	}

	@Override
	public int countTotalSystemTopicUnderNode(String topicUuid) {
		// get topic route
		if(StringUtils.isNotBlank(topicUuid)){
			Topic topic = getTopicByUuid(topicUuid);
			if(topic!=null){
				StringBuilder q = new StringBuilder("SELECT COUNT(*) FROM Topic WHERE topicroute LIKE '").append(topic.getTopicroute()).append("%'");
				Query query = entityManager.createQuery(q.toString());
				Number countResult = (Number)query.getSingleResult();
				if(countResult!=null) return countResult.intValue()-1; // don't count self
			}
		}
		
		
		return 0;
	}

	@Override
	public void delSubscribe(Long subscribeid) {
		if(subscribeid!=null){
			Subscribe subscribe = entityManager.find(Subscribe.class, subscribeid);
			if(subscribe!=null) entityManager.remove(subscribe);
		}
	}

	@Override
	public Subscribe getSubscribesByTopicUuidAccountId(String topicUuid, Long accountId) {
		if(StringUtils.isNotBlank(topicUuid) && accountId!=null){
			StringBuilder q = new StringBuilder("FROM Subscribe WHERE topicuuid =:topicUuid AND account_id =:accountId");
			Query query = entityManager.createQuery(q.toString()).setParameter("topicUuid", topicUuid).setParameter("accountId", accountId);
			List<Subscribe> subscribes = query.getResultList();
			if(subscribes!=null && subscribes.size()>0) return subscribes.get(0);
		}
		return null;
	}

	@Override
	public Long saveSubscribe(Subscribe subscribe) {
		if(subscribe!=null){
			if(subscribe.getId()!=null){
				return entityManager.merge(subscribe).getId();
			}else{
				entityManager.persist(subscribe);
				return subscribe.getId();
			}
		}
		return null;
	}

	@Override
	public Long saveMessageBody(Msgbody msgBody) {
		if(msgBody!=null){
			if(msgBody.getId()!=null){
				return entityManager.merge(msgBody).getId();
			}else{
				entityManager.persist(msgBody);
				return msgBody.getId();
			}
		}
		
		return null;
	}

	@Override
	public int countMsg(Long accountId, MsgType type) {
		// get topic route
		if(accountId!=null){
			
			StringBuilder q = new StringBuilder("SELECT COUNT(*) FROM Message WHERE account_id = ")
			.append(accountId);
			if(type!=null){
				q.append(" AND msgtype = '").append(type.getCode()).append("'");
			}
			Query query = entityManager.createQuery(q.toString());
			Number countResult = (Number)query.getSingleResult();
			if(countResult!=null){
				return countResult.intValue();
			}
		}
		
		return 0;
	}


}

package com.bizislife.core.hibernate.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.bizislife.core.controller.component.ApiResponse;
import com.bizislife.core.hibernate.pojo.Account;
import com.bizislife.core.hibernate.pojo.PaymentHistory;
import com.bizislife.core.hibernate.pojo.PaymentPlan;
import com.bizislife.core.hibernate.pojo.Permission;

@Repository("paymentDao")
public class PaymentDaoImpl implements PaymentDao{
    private static final Logger logger = LoggerFactory.getLogger(PaymentDaoImpl.class);

    @PersistenceContext
	private EntityManager entityManager;

	@Override
	public ApiResponse savePaymentPlan(PaymentPlan plan) {
		ApiResponse apires = new ApiResponse();
		apires.setSuccess(false);
		
		if(plan!=null){
			Long planId = null;
			if(plan.getId()!=null){
				planId = entityManager.merge(plan).getId();
			}else{
				entityManager.persist(plan);
				planId = plan.getId();
			}
			if(planId!=null){
				apires.setSuccess(true);
				apires.setResponse1(planId);
			}else{
				apires.setResponse1("System can't save the paymentPlan, this could be system issue!");
			}
		}else{
			apires.setResponse1("paymentPlan can't be null");
		}
		
		return apires;
	}

	@Override
	public List<PaymentHistory> findOrgPaymentHistories(Long orgId) {
		if(orgId!=null){
			StringBuilder q = new StringBuilder("FROM PaymentHistory where orgid = ").append(orgId);
			Query query = entityManager.createQuery(q.toString());
			return query.getResultList();
		}
		return null;
	}

	@Override
	public List<PaymentPlan> findFreePaymentPlans() {
		StringBuilder q = new StringBuilder("FROM PaymentPlan where dailypayment is null and monthlypayment is null");
		Query query = entityManager.createQuery(q.toString());
		return query.getResultList();
	}

	@Override
	public PaymentPlan getDefaultPaymentPlan() {
		StringBuilder q = new StringBuilder("FROM PaymentPlan where defaultplan = '").append(PaymentPlan.defaultPlan.yes.getCode()).append("'");
		Query query = entityManager.createQuery(q.toString());
		List<PaymentPlan> plans = query.getResultList();
		if(plans!=null && plans.size()>0){
			return plans.get(0);
		}
		
		return null;
	}

	@Override
	public PaymentPlan getPaymentPlan(Long planId) {
		if(planId!=null){
//			Account account = hibernateTemplate.get(Account.class, accountId);
			return entityManager.find(PaymentPlan.class, planId);
		}
		return null;
	}

}

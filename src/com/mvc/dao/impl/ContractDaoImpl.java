package com.mvc.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mvc.dao.ContractDao;
import com.mvc.entity.AlarmStatistic;
import com.mvc.entity.Contract;

/**
 * 合同
 * 
 * @author wangrui
 * @date 2016年9月13日
 */
@Repository("contractDaoImpl")
public class ContractDaoImpl implements ContractDao {
	@Autowired
	@Qualifier("entityManagerFactory")
	EntityManagerFactory emf;

	// 根据合同id修改状态
	public Boolean updateState(Integer cont_id, Integer cont_state) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		try {
			em.getTransaction().begin();
			sql.append("update contract c set c.cont_state=:cont_state where c.cont_id=:cont_id");
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter("cont_state", cont_state);
			query.setParameter("cont_id", cont_id);
			query.executeUpdate();
			em.flush();
			em.getTransaction().commit();
		} finally {
			em.close();
		}
		return true;
	}

	// 返回欠款合同信息
	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> findAllDebtCont(String contName, Integer offset, Integer end) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select * from contract c where c.cont_state=0 and c.cont_id in (select distinct(rn.cont_id) from receive_node rn ");
		sql.append("where rn.reno_time<=now() and rn.reno_state in (0,2)) and c.cont_ishistory=0");
		if (null != contName) {
			sql.append(" and c.cont_name like '%" + contName + "%'");
		}
		sql.append(" order by cont_id desc limit :offset,:end");
		Query query = em.createNativeQuery(sql.toString(), Contract.class);
		query.setParameter("offset", offset).setParameter("end", end);
		List<Contract> list = query.getResultList();
		em.close();
		return list;
	}

	// 返回逾期合同信息
	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> findAllOverdueCont(String contName, Integer offset, Integer end) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select * from contract c where c.cont_state=0 and c.cont_id in (select distinct(ps.cont_id) from project_stage ps where ps.prst_etime<=now()");
		sql.append(" and ps.prst_state=0) and c.cont_ishistory=0");
		if (contName != null) {
			sql.append(" and c.cont_name like '%" + contName + "%'");
		}
		sql.append(" order by cont_id desc limit :offset,:end");
		Query query = em.createNativeQuery(sql.toString(), Contract.class);
		query.setParameter("offset", offset).setParameter("end", end);
		List<Contract> list = query.getResultList();
		em.close();
		return list;
	}

	// 根据合同名获取合同信息
	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> findConByName(String contName, Integer offset, Integer end) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		sql.append("select * from contract c where c.cont_state=0 and c.cont_ishistory=0");// 在建
		if (null != contName) {
			sql.append(" and c.cont_name like '%" + contName + "%'");
		}
		sql.append(" order by cont_id desc limit :offset,:end");
		Query query = em.createNativeQuery(sql.toString(), Contract.class);
		query.setParameter("offset", offset).setParameter("end", end);
		List<Contract> list = query.getResultList();
		em.close();
		return list;
	}

	// 根据创建者ID和合同名查询合同总条数
	@Override
	public Long countTotal(String contName, Integer methodType) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		sql.append("select count(cont_id) from contract c where c.cont_ishistory=0 ");
		if (methodType == 1) {// 根据name查询
			sql.append(" and c.cont_state=0");
		} else if (methodType == 2) {// 查询欠款
			sql.append(
					" and c.cont_state=0 and c.cont_id in (select distinct(rn.cont_id) from receive_node rn where rn.reno_time<=now() and rn.reno_state in (0,2))");
		} else if (methodType == 3) {// 查询逾期
			sql.append(
					" and c.cont_state=0 and c.cont_id in (select distinct(t.cont_id) from task t where t.task_etime<=now() and t.task_state in (0,1) and t.task_isdelete=0)");
		} else if (methodType == 4) {// 终结合同
			sql.append(" and c.cont_state=1");// 竣工
		} else if (methodType == 5) {// 停建合同
			sql.append(" and c.cont_state=2");// 停建
		}
		if (contName != null) {
			sql.append(" and c.cont_name like '%" + contName + "%'");
		}
		Query query = em.createNativeQuery(sql.toString());
		BigInteger totalRow = (BigInteger) query.getSingleResult();// count返回值为BigInteger类型
		em.close();
		return totalRow.longValue();
	}

	// 删除合同
	@Override
	public Boolean delete(Integer cont_id) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		try {
			em.getTransaction().begin();
			sql.append("update contract c set c.cont_ishistory=1 where c.cont_id=:cont_id");
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter("cont_id", cont_id);
			query.executeUpdate();
			em.flush();
			em.getTransaction().commit();
		} finally {
			em.close();
		}
		return true;
	}

	// 查询所有终结合同列表
	@SuppressWarnings("unchecked")
	@Override
	public List<Contract> findAllEndCont(String contName, Integer offset, Integer end) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		sql.append("select * from contract c where c.cont_state=1 and c.cont_ishistory=0");
		if (contName != null) {
			sql.append(" and c.cont_name like '%" + contName + "%'");
		}
		sql.append(" order by cont_id desc limit :offset,:end");
		Query query = em.createNativeQuery(sql.toString(), Contract.class);
		query.setParameter("offset", offset).setParameter("end", end);
		List<Contract> list = query.getResultList();
		em.close();
		return list;
	}

	// 修改合同基本信息
	@Override
	public Boolean updateConById(Integer cont_id, Contract contract) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.merge(contract);
			em.getTransaction().commit();
		} finally {
			em.close();
		}
		return true;
	}

	// 报警统计
	@Override
	public AlarmStatistic findAlst(Integer user_id) {
		EntityManager em = emf.createEntityManager();
		StringBuilder sql = new StringBuilder();
		// 当前用户接收的所有任务
		sql.append(
				"select count(task_id) as totalReceiveTaskNum from task where task_isdelete=0 and task_state=1 and receiver_id=:user_id ");
		// 文书任务
		sql.append(
				"select count(task_id) as assistantTaskNum from task where task_isdelete=0 and task_state=0 and receiver_id=:user_id and task_type=1 ");
		// 执行管控任务
		sql.append(
				"select count(task_id) as managerControlTaskNum from task where task_isdelete=0 and task_state=0 and receiver_id=:user_id and task_type=2  ");
		// 普通任务
		sql.append(
				"select count(task_id) as otherTaskNum from task where task_isdelete=0 and task_state=0 and receiver_id=:user_id and task_type=3 ");

		// 待审核发票任务
		sql.append("select count(invo_id) as waitAuditBillTaskNum from invoice where invo_isdelete=0 and audit_id=:user_id and invo_state=0 ");
		// 发票任务
		sql.append("select count(invo_id) as billTaskNum from invoice where invo_isdelete=0 and audit_id=:user_id and invo_state=1 ");

		// 收款超时
		sql.append(
				"select count(*) as debtAlarmNum from (select count(alar_id) from alarm a where receiver_id=:user_id and alar_isremove=0 and alar_code in(2,3) group by task_id,reno_id,prst_id) as tmp ");
		// 工程逾期
		sql.append(
				"select count(*) as overdueAlarmNum from (select count(alar_id) from alarm a where receiver_id=:user_id and alar_isremove=0 and alar_code in(4,5) group by task_id,reno_id,prst_id) as tmp ");
		// 任务超时
		sql.append(
				"select count(*) as taskAlarmNum from (select count(alar_id) from alarm a where receiver_id=:user_id and alar_isremove=0 and alar_code=1 group by task_id,reno_id,prst_id) as tmp ");

		Query query = em.createNativeQuery(sql.toString(), Contract.class);
		query.setParameter("user_id", user_id);
		AlarmStatistic alarmStatistic = (AlarmStatistic) query.getSingleResult();
		em.close();
		return alarmStatistic;
	}

}

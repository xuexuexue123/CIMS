package com.mvc.service;

import java.util.List;

import com.mvc.entity.Contract;

public interface ContractService {

	// 查询所有欠款合同列表
	List<Contract> findAllDebtCont(Integer creator_id, String contName, Integer offset, Integer end);

	// 查询所有逾期合同列表
	List<Contract> findAllOverdueCont(Integer creator_id, String contName, Integer offset, Integer end);

	// 查询合同总条数
	Long countTotal(Integer creator_id, String contName, String methodType);

	// 根据合同名获取合同信息
	List<Contract> findConByName(Integer creator_id, String contName, Integer offset, Integer end);

	// 添加合同
	Boolean addContract(Contract contract);

	// 根据合同ID获取合同
	Contract selectContById(Integer cont_id);

	// 根据合同ID删除合同
	Boolean deleteContract(Integer cont_id);
}

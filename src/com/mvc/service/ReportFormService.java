package com.mvc.service;

import java.util.List;
import java.util.Map;

<<<<<<< HEAD
import org.springframework.http.ResponseEntity;

import com.mvc.entity.ProjectStatisticForm;
import com.utils.Pager;

import net.sf.json.JSONObject;
=======
import com.mvc.entity.ComoCompareRemo;
import com.mvc.entity.PlanProjectForm;
>>>>>>> f18b41a8e0e1003b483275e365270fd6ad064cf0

/**
 * 报表业务层
 * 
 * @author wangrui
 * @date 2016-11-15
 */
public interface ReportFormService {

<<<<<<< HEAD
	// 导出光电院项目分项统计表
	ResponseEntity<byte[]> exportProjectStatistic(Map<String, Object> map, String path);

	// 查询光电院项目分项统计表
	List<ProjectStatisticForm> findProjectStatistic(Map<String, Object> map, Pager pager, String path);

	// 将JSONObject转成Map
	Map<String, Object> JsonObjToMap(JSONObject jsonObject);

	// 查询报表页码相关
	Pager pagerTotal(Map<String, Object> map, Integer page);
=======
	// 光电院承担规划项目表
	List<PlanProjectForm> findPlanProject(Integer cont_state, Date startTime, Date endTime);

	// 根据日期获取合同额到款对比表
	ComoCompareRemo findByDate(Date oneTime, Date twoTime);
>>>>>>> f18b41a8e0e1003b483275e365270fd6ad064cf0
}

/**
 * 
 */
package com.dianping.tiger.event;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.dianping.tiger.ScheduleServer;
import com.dianping.tiger.dispatch.DispatchMultiService;
import com.dianping.tiger.dispatch.DispatchSingleService;
import com.dianping.tiger.dispatch.DispatchTaskEntity;
import com.dianping.tiger.dispatch.DispatchTaskService;
import com.dianping.tiger.utils.ScheduleConstants;

/**
 * @author yuantengkai 任务捞取类<br/>
 * <mail to: zjytk05@163.com/>
 */
public class EventFetcher {

	public static final int TASK_NUM = 200;

	private DispatchTaskService dispatchTaskService;

	public EventFetcher(DispatchTaskService dispatchTaskService) {
		this.dispatchTaskService = dispatchTaskService;
	}

	/**
	 * 获取对应的任务
	 * 
	 * @param handlerName
	 *            执行器名称
	 * @param nodeList
	 *            执行器节点
	 * @return
	 */
	public List<DispatchTaskEntity> getTasks(String handlerName,
			List<Integer> nodeList) {
		if (ScheduleServer.getInstance().getTaskStrategy() == ScheduleConstants.TaskFetchStrategy.Multi
				.getValue()) {// 各个执行器捞取策略
			if (StringUtils.isBlank(handlerName) || nodeList == null
					|| nodeList.isEmpty()) {
				throw new IllegalArgumentException(
						"handlerName or nodeList is empty.");
			}
			DispatchMultiService dispatchMultiService = (DispatchMultiService) dispatchTaskService;
			List<DispatchTaskEntity> tasks = dispatchMultiService
					.findDispatchTasksWithLimit(handlerName, nodeList, TASK_NUM);
			if (tasks == null) {
				return Collections.emptyList();
			}
			return tasks;
		}
		//单个执行器统一捞取策略
		if (nodeList == null || nodeList.isEmpty()) {
			throw new IllegalArgumentException("nodeList is empty.");
		}
		DispatchSingleService dispatchSingleService = (DispatchSingleService) dispatchTaskService;
		List<DispatchTaskEntity> tasks = dispatchSingleService
				.findDispatchTasksWithLimit(nodeList, TASK_NUM);
		if (tasks == null) {
			return Collections.emptyList();
		}
		return tasks;
	}

	/**
	 * 反压获取任务名称
	 * 
	 * @param handlerName
	 * @param nodeList
	 * @param taskId
	 * @return
	 */
	public List<DispatchTaskEntity> getTasksByBackFetch(String handlerName,
			List<Integer> nodeList, long taskId) {
		if (ScheduleServer.getInstance().getTaskStrategy() == ScheduleConstants.TaskFetchStrategy.Multi
				.getValue()) {// 各个执行器捞取策略
			if (StringUtils.isBlank(handlerName) || nodeList == null
					|| nodeList.isEmpty() || taskId < 1) {
				throw new IllegalArgumentException(
						"backFetch task,handlerName or nodeList is empty,or taskId smaller than 1");
			}
			DispatchMultiService dispatchMultiService = (DispatchMultiService) dispatchTaskService;
			List<DispatchTaskEntity> tasks = dispatchMultiService
					.findDispatchTasksWithLimitByBackFetch(handlerName,
							nodeList, TASK_NUM / 2, taskId);
			if (tasks == null) {
				return Collections.emptyList();
			}
			return tasks;
		}
		//单个执行器统一捞取策略
		if (nodeList == null || nodeList.isEmpty() || taskId < 1) {
			throw new IllegalArgumentException(
					"backFetch task, nodeList is empty,or taskId smaller than 1");
		}
		DispatchSingleService dispatchSingleService = (DispatchSingleService) dispatchTaskService;
		List<DispatchTaskEntity> tasks = dispatchSingleService
				.findDispatchTasksWithLimitByBackFetch(nodeList, TASK_NUM / 2,
						taskId);
		if (tasks == null) {
			return Collections.emptyList();
		}
		return tasks;
	}

}

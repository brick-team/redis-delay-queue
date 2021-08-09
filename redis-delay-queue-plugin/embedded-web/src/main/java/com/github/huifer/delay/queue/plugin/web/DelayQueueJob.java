package com.github.huifer.delay.queue.plugin.web;

public class DelayQueueJob {
	/**
	 * 任务id
	 */
	private String taskId;
	/**
	 * 任务类型
	 */
	private String taskType;
	/**
	 * 执行时间，16位时间戳
	 */
	private long delayTime;
	/**
	 * 参数,json表示
	 */
	private String params;

	/**
	 * 应用名称
	 */
	private String appName;

	@Override
	public String toString() {
		return "DelayQueueJob{" +
				"taskId='" + taskId + '\'' +
				", taskType='" + taskType + '\'' +
				", delayTime=" + delayTime +
				", params='" + params + '\'' +
				", appName='" + appName + '\'' +
				'}';
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
}

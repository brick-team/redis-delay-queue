package com.github.huifer.delay.queue.domain;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DelayQueueJob {
	/**
	 * 任务id
	 */
	@NotNull(message = "任务id不能为空")
	private String taskId;
	/**
	 * 任务类型
	 */
	@NotNull(message = "任务类型不能为空")
	private String taskType;
	/**
	 * 执行时间，16位时间戳
	 */
	private long delayTime;
	/**
	 * 参数,json表示
	 */
	private String params;
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
}

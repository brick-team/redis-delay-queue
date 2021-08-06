package com.github.huifer.delay.queue.plugin.domain;

import lombok.Data;


@Data
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

	@Override
	public String toString() {
		return "DelayQueueJob{" +
				"taskId='" + taskId + '\'' +
				", taskType='" + taskType + '\'' +
				", delayTime=" + delayTime +
				", params='" + params + '\'' +
				'}';
	}
}

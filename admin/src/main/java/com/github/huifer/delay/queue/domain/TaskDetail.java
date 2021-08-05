package com.github.huifer.delay.queue.domain;

import lombok.Data;

@Data
public class TaskDetail {

	/**
	 * 任务id
 	 */
	private String taskId;
	/**
	 * 执行时间，16位时间戳
	 */
	private long delayTime;
}

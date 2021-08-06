package com.github.huifer.delay.queue.plugin.web;


public interface TaskWorker {
	/**
	 * 任务处理
	 */
	void invoke(String message);

	/**
	 * 定时任务类型
	 */
	String type();


	/**
	 * 填写当前类
	 */
	Class<?> clazz();

}

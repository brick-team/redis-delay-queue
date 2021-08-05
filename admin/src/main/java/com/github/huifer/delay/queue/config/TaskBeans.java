package com.github.huifer.delay.queue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TaskBeans {

	@Bean(value = "taskExecutorService")
	public ExecutorService taskExecutorService(DelayQueueConfig delayQueueConfig) {
		ExecutorService executorService = Executors.newFixedThreadPool(delayQueueConfig.getThreadSize());
		return executorService;
	}
}

package com.github.huifer.delay.queue.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "delayQueueConfig")
@ConfigurationProperties(prefix = "delay-queue")
public class DelayQueueConfig {
	private int threadSize = 10;

	public int getThreadSize() {
		return threadSize;
	}

	public void setThreadSize(int threadSize) {
		this.threadSize = threadSize;
	}
}

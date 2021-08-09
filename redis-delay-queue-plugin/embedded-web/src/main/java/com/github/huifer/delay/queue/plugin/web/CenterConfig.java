package com.github.huifer.delay.queue.plugin.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("delay-queue")
public class CenterConfig {
	private String centerUrl;

	public String getCenterUrl() {
		return centerUrl;
	}

	public void setCenterUrl(String centerUrl) {
		this.centerUrl = centerUrl;
	}
}

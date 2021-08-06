package com.github.huifer.delay.queue.plugin.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("delay-queue:")
public class CenterConfig {
	private String centerUrl;

	public String getCenterUrl() {
		return centerUrl;
	}

	public void setCenterUrl(String centerUrl) {
		this.centerUrl = centerUrl;
	}
}

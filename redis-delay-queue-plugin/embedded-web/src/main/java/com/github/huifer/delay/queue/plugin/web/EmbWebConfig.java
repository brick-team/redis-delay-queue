package com.github.huifer.delay.queue.plugin.web;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@ComponentScan("com.github.huifer.delay.queue.plugin.web")
@EnableConfigurationProperties(value = {
		CenterConfig.class
})
public class EmbWebConfig {

	@Bean(value = "delayQueueTemplate")
	public RestTemplate delayQueueTemplate() {
		return new RestTemplate();
	}
}

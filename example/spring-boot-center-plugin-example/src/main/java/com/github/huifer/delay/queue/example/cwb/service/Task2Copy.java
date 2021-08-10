package com.github.huifer.delay.queue.example.cwb.service;

import com.github.huifer.delay.queue.plugin.web.TaskWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Task2Copy implements TaskWorker {
	@Override
	@Async
	public void invoke(String message) {
		log.info("message=[{}]", message);
	}

	@Override
	public String type() {
		return "task2";
	}

	@Override
	public Class<?> clazz() {
		return Task2Copy.class;
	}
}

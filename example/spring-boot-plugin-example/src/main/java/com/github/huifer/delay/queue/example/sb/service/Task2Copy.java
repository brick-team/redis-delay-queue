package com.github.huifer.delay.queue.example.sb.service;

import com.github.huifer.delay.queue.plugin.service.TaskWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Task2Copy implements TaskWorker {
	@Override
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

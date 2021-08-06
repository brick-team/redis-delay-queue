package com.github.huifer.delay.queue.example.sb.service;

import com.github.huifer.delay.queue.plugin.service.TaskWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class Task1 implements TaskWorker {
	@Override
	public void invoke(String message) {
		log.info("message = [{}]", message);
	}

	@Override
	public String type() {
		return "task1";
	}

	@Override
	public Class<?> clazz() {
		return Task2.class;
	}
}

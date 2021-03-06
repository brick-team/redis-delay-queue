package com.github.huifer.delay.queue.example.cwb.service;

import com.github.huifer.delay.queue.plugin.web.TaskWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Task2 implements TaskWorker {
	@Override
	public void invoke(String message) {
		log.info("message = [{}]", message);
	}

	@Override
	public String type() {
		return "task2";
	}

	@Override
	public Class<?> clazz() {
		return Task2.class;
	}
}

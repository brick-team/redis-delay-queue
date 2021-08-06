package com.github.huifer.delay.queue.plugin.web;

import com.netflix.appinfo.ApplicationInfoManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
@RestController
@RequestMapping("/hf/delay_queue")
public class DynamicMetadataReporter implements ApplicationContextAware {
	private static final Logger log = LoggerFactory.getLogger(DynamicMetadataReporter.class);
	private final ApplicationInfoManager aim;
	Map<String, TaskWorker> beansOfType;
	private ApplicationContext applicationContext;

	public DynamicMetadataReporter(ApplicationInfoManager aim) {
		this.aim = aim;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@PostMapping("/work")
	public void work(
			@RequestBody DelayQueueJob delayQueue
	) {
		List<TaskWorker> taskWorkers = findTaskWorkers("");
		for (TaskWorker taskWorker : taskWorkers) {
			try {
				taskWorker.invoke(delayQueue.getParams());
				log.info("任务执行成功,任务类型=[{}],任务id=[{}],执行类是=[{}],执行参数是=[{}]",
						delayQueue.getTaskType(),
						delayQueue.getTaskType(),
						taskWorker.clazz().toString(),
						delayQueue.getParams()
				);
			} catch (Exception e) {
				log.error("任务执行失败,任务类型=[{}],任务id=[{}],执行类是=[{}],执行参数是=[{}]",
						delayQueue.getTaskType(),
						delayQueue.getTaskType(),
						taskWorker.clazz().toString(),
						delayQueue.getParams()
				);
				log.error("e ", e);
			}

		}

	}

	private List<TaskWorker> findTaskWorkers(String taskType) {
		Map<String, TaskWorker> beansOfType = applicationContext.getBeansOfType(TaskWorker.class);
		List<TaskWorker> taskWorkers = new ArrayList<>();

		beansOfType.forEach((k, v) -> {
			boolean equals = v.type().equals(taskType);
			if (equals) {
				taskWorkers.add(v);
			}
		});
		return taskWorkers;
	}

	private List<String> taskTypes() {
		Map<String, TaskWorker> beansOfType = applicationContext.getBeansOfType(TaskWorker.class);
		this.beansOfType = beansOfType;
		List<String> collect = beansOfType.values().stream().map(TaskWorker::type).collect(Collectors.toList());
		return collect;
	}


	@PostConstruct
	public void init() {
		Map<String, String> map = aim.getInfo().getMetadata();
		List<String> strings = taskTypes();
		StringJoiner stringJoiner = new StringJoiner(",");

		StringJoiner sj = new StringJoiner(", ");
		for (String name : strings) {
			sj.add(name);
		}
		map.put("delay-queue:task", sj.toString());
	}
}
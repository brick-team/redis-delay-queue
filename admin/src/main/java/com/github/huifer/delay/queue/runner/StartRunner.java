package com.github.huifer.delay.queue.runner;

import com.github.huifer.delay.queue.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class StartRunner implements ApplicationRunner {
	@Autowired
	private TaskSubmitService taskSubmitService;
	@Autowired
	private ApplicationContext context;
	@Autowired
	private DelayQueueTypeService delayQueueTypeService;

	@Autowired
	@Qualifier("taskExecutorService")
	private ExecutorService taskExecutorService;

	@Autowired
	private DelayBucketService bucketService;

	@Autowired
	private DelayQueuePoolService poolService;

	@Autowired
	private RedisLockServiceImpl redisLockHelper;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Set<String> list = delayQueueTypeService.list();
		log.info("已存在的任务类型=[{}]", list);
		if (!CollectionUtils.isEmpty(list)) {
			for (String taskType : list) {
				taskExecutorService.execute(new DelayBucketHandler(
						taskType,
						bucketService,
						poolService,
						1,
						redisLockHelper, findDelayQueueJobHandler()));
			}
		}
	}

	private Collection<DelayQueueJobHandler> findDelayQueueJobHandler() {
		Map<String, DelayQueueJobHandler> beansOfType = context.getBeansOfType(DelayQueueJobHandler.class);
		Collection<DelayQueueJobHandler> values = beansOfType.values();
		return values;
	}
}

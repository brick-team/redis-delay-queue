package com.github.huifer.delay.queue.example.sb.ctr;

import com.github.huifer.delay.queue.plugin.domain.DelayQueueJob;
import com.github.huifer.delay.queue.plugin.service.TaskSubmitService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobCtr {
	final TaskSubmitService taskSubmitService;

	public JobCtr(TaskSubmitService taskSubmitService) {
		this.taskSubmitService = taskSubmitService;
	}

	@GetMapping("/add")
	public void addJob(@RequestBody DelayQueueJob delayQueueJobEntity) {
		this.taskSubmitService.submitTask(delayQueueJobEntity);
	}
}

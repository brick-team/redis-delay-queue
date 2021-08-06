package com.github.huifer.delay.queue.example.cwb.ctr;

import com.github.huifer.delay.queue.plugin.web.DelayQueueJob;
import com.github.huifer.delay.queue.plugin.web.TaskSubmitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobCtr {

	@Autowired
	TaskSubmitService taskSubmitService;

	@GetMapping("/add")
	public void addJob(@RequestBody DelayQueueJob delayQueueJobEntity) {
		taskSubmitService.send(delayQueueJobEntity);
	}
}

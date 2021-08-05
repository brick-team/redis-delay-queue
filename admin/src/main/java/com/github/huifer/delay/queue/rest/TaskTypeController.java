package com.github.huifer.delay.queue.rest;

import com.github.huifer.delay.queue.service.DelayQueueTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/task/type")
public class TaskTypeController {
	private final DelayQueueTypeService delayQueueTypeService;

	public TaskTypeController(DelayQueueTypeService delayQueueTypeService) {
		this.delayQueueTypeService = delayQueueTypeService;
	}

	@GetMapping("/list")
	public ResponseEntity list() {
		return ResponseEntity.ok(delayQueueTypeService.list());
	}
}

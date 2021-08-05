package com.github.huifer.delay.queue.rest;

import com.github.huifer.delay.queue.domain.ServerRegisterParam;
import com.github.huifer.delay.queue.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/server")
public class ServerController {
	private final ServerService serverService;

	public ServerController(ServerService serverService) {
		this.serverService = serverService;
	}

	@PostMapping("/register")
	public ResponseEntity register(
			@RequestBody ServerRegisterParam serverRegisterParam
	) {
		List<String> taskType = serverRegisterParam.getTaskType();
		for (String type : taskType) {

			this.serverService.register(type, serverRegisterParam.getUrl());
		}
		return ResponseEntity.ok(true);
	}


	@GetMapping("/query")
	public ResponseEntity query(String taskType) {
		return ResponseEntity.ok(this.serverService.list(taskType));
	}

	@PostMapping("/beat")
	public ResponseEntity beat(@RequestBody ServerRegisterParam serverRegisterParam) {
		List<String> taskType = serverRegisterParam.getTaskType();
		for (String type : taskType) {
			this.serverService.register(type, serverRegisterParam.getUrl());
		}
		return ResponseEntity.ok(true);
	}
}

package com.github.huifer.delay.queue.domain;

import lombok.Data;

import java.util.List;

@Data
public class ServerRegisterParam {
	private String url;
	private List<String> taskType;
}

package com.github.huifer.delay.queue.service;

import com.github.huifer.delay.queue.domain.DelayQueueJob;

public interface DelayQueueJobHandler {
	void work(DelayQueueJob delayQueueJob);


}

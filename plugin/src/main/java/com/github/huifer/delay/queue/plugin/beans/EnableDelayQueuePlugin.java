package com.github.huifer.delay.queue.plugin.beans;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DelayQueuePluginBeans.class)
public @interface EnableDelayQueuePlugin {
}

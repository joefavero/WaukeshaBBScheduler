/*
 * Copyright 2022-2023 Obsidian Solution Inc
 * Consulting work for Tulsa Tech Progress Tool
 */
package com.obsidiansoln.web;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsynchConfiguration 
{
	
  @Bean(name = "asyncExecutor1")
  public Executor asyncExecutor1() 
  {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(100);
    executor.setMaxPoolSize(8000);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("AsynchThread1-");
    executor.initialize();
    return executor;
  }
  
  @Bean(name = "asyncExecutor2")
  public Executor asyncExecutor2() 
  {
	  ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(1);
    executor.setMaxPoolSize(1);
    executor.setQueueCapacity(10);
    executor.setThreadNamePrefix("AsynchThread2-");
    executor.initialize();
    return executor;
  }
  
}
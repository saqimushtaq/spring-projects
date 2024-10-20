package com.saqib.threads;

import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.GenericContainer;

public class TestThreadsApplication {

	public static void main(String[] args) {
    runWithVirtualThreads(true);
	}

  static void runWithVirtualThreads(boolean enabled){
    var port = 8080;
    var httpbin = new GenericContainer<>("mccutchen/go-httpbin")
      .withExposedPorts(port);
    httpbin.start();
    var threads = Integer.toString(Runtime.getRuntime().availableProcessors());
    System.setProperty("server.tomcat.threads.max", threads);
    System.setProperty("server.virtualThreadScheduler.maxPoolSize", threads);
    System.setProperty("spring.threads.virtual.enabled", Boolean.toString(enabled));
    System.setProperty("httpbin.url", "http://" + httpbin.getHost() + ":" + httpbin.getMappedPort(port));
    SpringApplication.from(ThreadsApplication::main).run();

  }

}

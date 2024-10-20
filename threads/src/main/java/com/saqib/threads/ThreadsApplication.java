package com.saqib.threads;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Objects;

@SpringBootApplication
public class ThreadsApplication {

  @Bean
  RestClient restClient(RestClient.Builder builder, @Value("${httpbin.url}") String url) {
    return builder
      .baseUrl(url)
      .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(ThreadsApplication.class, args);
  }

  @RestController
  class DelayController {
    @Autowired
    RestClient restClient;
    Logger log = LoggerFactory.getLogger(DelayController.class);

    @GetMapping("{seconds}")
    public Map<String, Object> delay(@PathVariable int seconds) {
      var request = restClient.get()
        .uri("/delay/" + seconds)
        .retrieve()
        .toEntity(String.class);
      log.info("{} on {}", request.getStatusCode(), Thread.currentThread());
      return Map.of("done", true);
    }
  }

}

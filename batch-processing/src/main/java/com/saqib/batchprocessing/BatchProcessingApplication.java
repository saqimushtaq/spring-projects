package com.saqib.batchprocessing;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BatchProcessingApplication {

  public static void main(String[] args) {
    SpringApplication.run(BatchProcessingApplication.class, args);
  }

}

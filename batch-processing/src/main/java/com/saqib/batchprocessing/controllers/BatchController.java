package com.saqib.batchprocessing.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customers")
@RequiredArgsConstructor
public class BatchController {
  final JobLauncher jobLauncher;
  final Job job;

  @GetMapping("batch")
  public void launchJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder().toJobParameters();
    jobLauncher.run(job, jobParameters);
  }
}

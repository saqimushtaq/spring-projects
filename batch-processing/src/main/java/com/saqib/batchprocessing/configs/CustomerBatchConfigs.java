package com.saqib.batchprocessing.configs;

import com.saqib.batchprocessing.entities.Customer;
import com.saqib.batchprocessing.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class CustomerBatchConfigs {

  private final CustomerRepository customerRepository;
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Bean
  @JobScope
  public FlatFileItemReader<Customer> customerReader(@Value("#{jobParameters['filePath']}") String path) {
    var reader = new FlatFileItemReader<Customer>();
    reader.setResource(new FileSystemResource(path));
    reader.setLinesToSkip(1);
    reader.setLineMapper(new DefaultLineMapper<Customer>() {{
      setLineTokenizer(new DelimitedLineTokenizer() {{
        setNames("id", "firstName", "lastName", "email", "phone");
      }});
      setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
        setTargetType(Customer.class);
      }});
    }});
    return reader;
  }

  @Bean
  public ItemProcessor<Customer, Customer> customerProcessor() {
    return customer -> customer;
  }

  @Bean
  public RepositoryItemWriter<Customer> customerWriter() {
    var writer = new RepositoryItemWriter<Customer>();
    writer.setRepository(customerRepository);
    writer.setMethodName("save");
    return writer;
  }

  @Bean
  public Step step() {
    return new StepBuilder("step-1", jobRepository)
      .<Customer, Customer>chunk(10, transactionManager)
      .reader(customerReader(null))
      .processor(customerProcessor())
      .writer(customerWriter())
      .build();
  }

  @Bean
  public Job job() {
    return new JobBuilder("customer-batch", jobRepository)
      .incrementer(new RunIdIncrementer())
      .flow(step())
      .end()
      .build();
  }
}

package com.saqib.batchprocessing.configs;

import lombok.RequiredArgsConstructor;
import org.apache.sshd.client.ClientBuilder;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.auth.password.PasswordIdentityProvider;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.client.keyverifier.ServerKeyVerifier;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.kex.BuiltinDHFactories;
import org.apache.sshd.common.signature.BuiltinSignatures;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.*;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

@Configuration
@RequiredArgsConstructor
public class SftpConfigs {

  final JobLauncher jobLauncher;
  final Job job;

  @Bean
  public SessionFactory<SftpClient.DirEntry> sftpSessionFactory() throws IOException {
    SshClient sshClient = SshClient.setUpDefaultClient();
    sshClient.setKeyExchangeFactories(NamedFactory.setUpTransformedFactories(false, BuiltinDHFactories.VALUES, ClientBuilder.DH2KEX));

    sshClient.setSignatureFactories(new ArrayList<>(BuiltinSignatures.VALUES));

    ServerKeyVerifier serverKeyVerifier = AcceptAllServerKeyVerifier.INSTANCE;

    sshClient.setServerKeyVerifier(serverKeyVerifier);
    sshClient.setPasswordIdentityProvider(PasswordIdentityProvider.wrapPasswords("pass"));

    DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(sshClient, true);

    factory.setHost("127.0.0.1");
    factory.setPort(2222);
    factory.setUser("foo");

    return new CachingSessionFactory<>(factory);
  }

  @Bean
  public SftpInboundFileSynchronizer ftpInboundFileSynchronizer() throws IOException {
    var fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
    fileSynchronizer.setRemoteDirectory("upload");
    fileSynchronizer.setDeleteRemoteFiles(true);
    fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter("*.csv"));
    return fileSynchronizer;
  }

  @Bean
  @InboundChannelAdapter(channel = "sftpChannel", poller = @Poller(fixedDelay = "5000"))
  public MessageSource<File> ftpMessageSource() throws IOException {
    SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(ftpInboundFileSynchronizer());
    source.setLocalDirectory(new File("/Users/saqi/Documents/sftp"));
    source.setAutoCreateLocalDirectory(true);
    source.setLocalFilter(new AcceptOnceFileListFilter<>());
    source.setMaxFetchSize(1);
    return source;
  }

  @Bean
  @ServiceActivator(inputChannel = "sftpChannel")
  public MessageHandler handler() {
    return message -> {
      System.out.println("reading file: " + message.getPayload());
      triggerJob(message);
    };
  }

  private void triggerJob(Message<?> message) {
    var file = (File) message.getPayload();
    if(!file.getName().endsWith(".csv") || file.isDirectory()) {
      System.out.println("Skipping file: " + file.getName());
      return;
    }
    String filePath = file.getAbsolutePath();
    JobParameters jobParameters = new JobParametersBuilder()
      .addString("filePath", filePath)
      .addLong("time", System.currentTimeMillis())
      .toJobParameters();
    try {
      jobLauncher.run(job, jobParameters);
    } catch (Exception e) {
      e.printStackTrace(); // Handle exceptions as necessary
    }

  }

  @MessagingGateway
  public interface MyGateway {

    @Gateway(requestChannel = "toSftpChannel")
    void sendToFtp(File file);

  }
}

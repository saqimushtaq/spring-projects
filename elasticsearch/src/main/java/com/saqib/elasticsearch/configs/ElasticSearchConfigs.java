package com.saqib.elasticsearch.configs;

import com.saqib.elasticsearch.ElasticsearchApplication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;

@Configuration
public class ElasticSearchConfigs extends ElasticsearchConfiguration {

  @Value("${spring.elasticsearch.client.certificate}")
  private String certificateBase64;

  @Override
  public ClientConfiguration clientConfiguration() {
    final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
      .connectedTo("localhost:9200")
      .usingSsl(getSSLContext())
      .withBasicAuth("elastic", "*p4l60l1*cFVE+sezFvC")
      .build();
    return clientConfiguration;
  }

  private SSLContext getSSLContext() {
    try {
//      byte[] decode = Base64.getDecoder().decode(certificateBase64);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      var cert = ResourceUtils.getFile("classpath:http_ca.crt");
      Certificate ca;
      try (InputStream certificateInputStream = new ByteArrayInputStream(Files.readAllBytes(cert.toPath()))) {
        ca = cf.generateCertificate(certificateInputStream);
      } catch (CertificateException | IOException e) {
        throw new RuntimeException(e);
      }

      String keyStoreType = KeyStore.getDefaultType();
      KeyStore keyStore = KeyStore.getInstance(keyStoreType);
      keyStore.load(null, null);
      keyStore.setCertificateEntry("ca", ca);

      String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
      TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(tmfAlgorithm);
      tmf.init(keyStore);

      SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, tmf.getTrustManagers(), null);
      return context;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

}

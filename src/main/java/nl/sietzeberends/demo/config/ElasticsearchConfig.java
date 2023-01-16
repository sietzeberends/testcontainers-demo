/*
 * ElasticsearchConfig.java
 *
 * Copyright © 2022 ING Group. All rights reserved.
 *
 * This software is the confidential and proprietary information of ING Group ("Confidential Information").
 */
package nl.sietzeberends.demo.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import java.net.URI;

@Configuration(proxyBeanMethods = false) public class ElasticsearchConfig extends ElasticsearchConfiguration {

  private final CustomElasticsearchProperties properties;

  @Autowired public ElasticsearchConfig(CustomElasticsearchProperties properties) {
    this.properties = properties;
  }

  @Override @NotNull public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo(properties.getHostAndPort())
        .withBasicAuth(properties.getUsername(), properties.getPassword())
        .withConnectTimeout(properties.getConnectionTimeout())
        .withSocketTimeout(properties.getSocketTimeout())
        .withPathPrefix(properties.getPathPrefix())
        .build();
  }

  @Configuration(proxyBeanMethods = false) @ConfigurationProperties(prefix = "elasticsearch") @Primary
  public static class CustomElasticsearchProperties extends ElasticsearchProperties {

    String[] getHostAndPort() {
      return getUris().stream().map(URI::create).map(this::getHostAndPort).toArray(String[]::new);
    }

    private String getHostAndPort(URI uri) {
      return uri.getHost() + ":" + calculatePort(uri);
    }

    private int calculatePort(URI uri) {
      if (uri.getPort() > 0) {
        return uri.getPort();
      }
      if ("http".equals(uri.getScheme())) {
        return 80;
      }
      return 443;
    }
  }
}

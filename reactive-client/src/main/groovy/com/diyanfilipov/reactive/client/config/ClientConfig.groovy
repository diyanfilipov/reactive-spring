package com.diyanfilipov.reactive.client.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class ClientConfig {

  @Bean
  WebClient walletClient() {
    WebClient.create("http://localhost:8086/wallet")
  }

}

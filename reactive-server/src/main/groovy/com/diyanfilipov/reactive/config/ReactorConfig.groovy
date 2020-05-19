package com.diyanfilipov.reactive.config

import com.diyanfilipov.reactive.dao.Message
import com.diyanfilipov.reactive.dao.Show
import com.diyanfilipov.reactive.handler.socket.MessageTrackerHandler
import com.diyanfilipov.reactive.handler.socket.ShowsCreatedTrackerHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy
import reactor.core.publisher.DirectProcessor
import reactor.core.publisher.FluxProcessor

@Configuration
class ReactorConfig {

  @Bean
  FluxProcessor<Show, Show> createdShowPublisher() {
    DirectProcessor.<Show>create().serialize()
  }

  @Bean
  FluxProcessor<Message, Message> messageSentPublisher() {
    DirectProcessor.<Message>create().serialize()
  }

  @Bean
  HandlerMapping handlerMapping() {
    Map<String, WebSocketHandler> map = [:]

    map.put("/trackCreatedShows", new ShowsCreatedTrackerHandler(createdShowPublisher()))
    map.put("/messages/{from}", new MessageTrackerHandler(messageSentPublisher()))

    SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping()
    mapping.setUrlMap(map)
    mapping.setOrder(-1) // before annotated controllers
    mapping
  }

  @Bean
  WebSocketHandlerAdapter handlerAdapter() {
    new WebSocketHandlerAdapter(new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy()))
  }



}

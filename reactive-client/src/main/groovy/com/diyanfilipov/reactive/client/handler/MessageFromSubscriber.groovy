package com.diyanfilipov.reactive.client.handler

import com.diyanfilipov.reactive.client.data.Message
import com.diyanfilipov.reactive.client.data.Show
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.Mono

@Slf4j
class MessageFromSubscriber implements WebSocketHandler {

  private ObjectMapper objectMapper = new ObjectMapper()
  private FluxProcessor<Message, Message> messageEmitter

  MessageFromSubscriber(FluxProcessor<Message, Message> messageEmitter) {
    this.messageEmitter = messageEmitter
  }

  @Override
  Mono<Void> handle(WebSocketSession session) {
    session
      .receive()
      .map({ wsm -> objectMapper.readValue(wsm.payloadAsText, Message) })
      .doOnNext( { show -> messageEmitter.onNext(show) })
      .log("INBOUND MESSAGES FROM CLIENT SESSION [${session.id}]")
      .then()
  }
}

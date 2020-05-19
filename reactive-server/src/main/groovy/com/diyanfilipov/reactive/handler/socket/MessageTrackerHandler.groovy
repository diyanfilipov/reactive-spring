package com.diyanfilipov.reactive.handler.socket

import com.diyanfilipov.reactive.dao.Message
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.util.UriTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.Mono

class MessageTrackerHandler implements WebSocketHandler {

  private ObjectMapper objectMapper = new ObjectMapper()

  private final FluxProcessor<Message, Message> messageSentPublisher

  MessageTrackerHandler(FluxProcessor<Message, Message> messageSentPublisher) {
    this.messageSentPublisher = messageSentPublisher
  }

  @Override
  Mono<Void> handle(WebSocketSession session) {
    String fromName = new UriTemplate("/messages/{from}").match(session.handshakeInfo.uri.path).get('from')

    Flux<Message> messageFlux = messageSentPublisher
      .log("MESSAGE SENT PUBLISHER")
      .share()

    session.send(
        messageFlux
          .filter({ message -> message.toName == fromName})
          .map({ message -> session.textMessage(objectMapper.writeValueAsString(message))})
      )
      .onTerminateDetach()
      .log("OUTBOUND SERVER SESSION [${session.id}]")
      .and(
        session
          .receive()
          .log("INBOUND SERVER SESSION [${session.id}]")
      )
  }
}

package com.diyanfilipov.reactive.handler.socket

import com.diyanfilipov.reactive.dao.Show
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.Mono

import java.time.Duration

class ShowsCreatedTrackerHandler implements WebSocketHandler {

  private ObjectMapper objectMapper = new ObjectMapper()

  private final FluxProcessor<Show, Show> showCreatedPublisher

  ShowsCreatedTrackerHandler(FluxProcessor<Show, Show> showCreatedPublisher) {
    this.showCreatedPublisher = showCreatedPublisher
  }

  @Override
  Mono<Void> handle(WebSocketSession session) {

    Flux<Show> showFlux = showCreatedPublisher
      .log("SHOW CREATED PUBLISHER")
      .share()

    session.send(showFlux.map({ show -> session.textMessage(objectMapper.writeValueAsString(show))}))
      .onTerminateDetach()
      .log("OUTBOUND SHOW CREATED SERVER SESSION [${session.id}]")
      .and(
        session
          .receive()
          .log("INBOUND SHOW CREATED SERVER SESSION [${session.id}]")
      )
  }
}

package com.diyanfilipov.reactive.client.handler

import com.diyanfilipov.reactive.client.data.Show
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.util.logging.Slf4j
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.Mono

@Slf4j
class TrackShowsSubscriber implements WebSocketHandler {

  private ObjectMapper objectMapper = new ObjectMapper()
  private FluxProcessor<Show, Show> trackShowsSubscriber

  TrackShowsSubscriber(FluxProcessor<Show, Show> trackShowsSubscriber) {
    this.trackShowsSubscriber = trackShowsSubscriber
  }

  @Override
  Mono<Void> handle(WebSocketSession session) {
    session
      .receive()
      .map({ wsm -> objectMapper.readValue(wsm.payloadAsText, Show) })
      .doOnNext( { show -> trackShowsSubscriber.onNext(show) })
      .log("INBOUND TRACK SHOWS CLIENT SESSION [${session.id}]")
      .then()
  }
}

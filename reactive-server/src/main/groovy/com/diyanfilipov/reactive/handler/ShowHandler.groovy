package com.diyanfilipov.reactive.handler

import com.diyanfilipov.reactive.dao.Show
import com.diyanfilipov.reactive.repository.ShowRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.Mono

import java.time.Duration

@Component
class ShowHandler {

  @Autowired(required = false)
  ShowRepository showRepository

  @Autowired
  FluxProcessor<Show, Show> createdShowPublisher

  Mono<ServerResponse> findShowReactive(ServerRequest serverRequest) {

    Optional<Show> showOptional = showRepository.findById(serverRequest.pathVariable('id'))

    showOptional.isPresent() ?
      ServerResponse.ok().body(BodyInserters.fromObject(showOptional.get())) :
      Mono.<ServerResponse>empty()
  }

  Mono<ServerResponse> findAllShowsReactive(ServerRequest serverRequest) {
    ServerResponse.ok().body(BodyInserters.fromObject(showRepository.findAll()))
  }

  Mono<ServerResponse> streamShows(ServerRequest serverRequest) {

    Flux<Show> showFlux = createdShowPublisher
      .onTerminateDetach()
      .bufferTimeout(20, Duration.ofSeconds(2))
      .log("SHOW CREATED HANDLER STREAM")
      .flatMap({ showList ->
        Flux.fromIterable(showList) })
      .share()

    ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM).body(BodyInserters.fromPublisher(showFlux, Show))
  }
}

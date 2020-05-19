package com.diyanfilipov.reactive.client.controller

import com.diyanfilipov.reactive.client.data.Show
import com.diyanfilipov.reactive.client.handler.TrackShowsSubscriber
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.Disposable
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor
import reactor.core.publisher.Mono

import java.time.Duration

@RestController
class ShowWebClientController {


  @GetMapping(path = "/webClient/trackShows", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Show> trackShows() {

    WebClient webClient = WebClient.builder()
      .baseUrl('http://localhost:8086/router/stream')
      .build()


    Flux<Show> showFlux = webClient
      .get()
      .retrieve()
      .bodyToFlux(Show)
      .log("WEB CLIENT SHOW")

    Disposable subscription = showFlux.subscribe()

    showFlux.doOnCancel({ subscription.dispose() })
  }
}

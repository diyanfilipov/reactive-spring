package com.diyanfilipov.reactive.client.controller

import com.diyanfilipov.reactive.client.data.Show
import org.reactivestreams.Subscription
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.Disposable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class ShowServerSentEventController {


  @GetMapping(path = "/sse/trackShows", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Show> trackShows() {

    WebClient webClient = WebClient.builder()
      .baseUrl('http://localhost:8086/sse/show')
      .build()


    Flux<Show> showFlux = webClient
      .get()
      .retrieve()
      .bodyToFlux(Show)
      .log("CLIENT SHOW SSE")

    Disposable subscription = showFlux.subscribe()

    showFlux.doOnCancel({ subscription.dispose() })
  }
}

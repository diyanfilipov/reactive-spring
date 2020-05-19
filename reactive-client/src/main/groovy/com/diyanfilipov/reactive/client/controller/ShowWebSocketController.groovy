package com.diyanfilipov.reactive.client.controller

import com.diyanfilipov.reactive.client.data.Show
import com.diyanfilipov.reactive.client.handler.TrackShowsSubscriber
import groovy.util.logging.Slf4j
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.Disposable
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor

import java.time.Duration

@RestController
class ShowWebSocketController {


  @GetMapping(path = "/trackShows", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Show> trackShows() {

    FluxProcessor<Show, Show> trackShowsEmitter = EmitterProcessor.<Show>create().serialize()

    ReactorNettyWebSocketClient webSocketClient = new ReactorNettyWebSocketClient()

    Disposable disposable = webSocketClient
      .execute(new URI("ws://localhost:8086/trackShows"), new TrackShowsSubscriber(trackShowsEmitter))
      .subscribe()

    trackShowsEmitter
      .log("SHOWS EMITTER")
      .doOnCancel({ -> disposable.dispose() })
      .bufferTimeout(20, Duration.ofSeconds(5))
      .flatMap({ showList -> Flux.fromIterable(showList) })
      .share()
  }
}

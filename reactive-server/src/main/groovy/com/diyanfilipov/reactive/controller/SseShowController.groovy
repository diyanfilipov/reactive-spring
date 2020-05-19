package com.diyanfilipov.reactive.controller

import com.diyanfilipov.reactive.dao.Show
import com.diyanfilipov.reactive.model.ShowRequest
import com.diyanfilipov.reactive.service.ShowService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor

import java.time.Duration

@RestController
@RequestMapping("/sse/show")
class SseShowController {

  @Autowired
  ShowService showService

  @Autowired
  FluxProcessor<Show, Show> createdShowPublisher

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Show> streamShows() {

    createdShowPublisher
      .<Show>map({ show ->
        ServerSentEvent
          .builder()
          .event('ShowCreated')
          .id(show.id)
          .data(show)
          .build()
        })
      .onTerminateDetach()
      .bufferTimeout(20, Duration.ofSeconds(2))
      .log("SSE SHOW CREATED")
      .flatMap({ showList ->
        Flux.fromIterable(showList) })
      .share()

//    Flux.fromIterable(
//      showService.findAllShows()
//    )
//    .<Show>map({ show ->
//      ServerSentEvent
//        .builder()
//        .event('ShowCreated')
//        .id(show.id)
//        .build()
//    })
//    .delayElements(Duration.ofSeconds(2))
//    .log()
  }
}

package com.diyanfilipov.reactive.client.controller

import com.diyanfilipov.reactive.client.data.Message
import com.diyanfilipov.reactive.client.handler.MessageFromSubscriber
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient
import reactor.core.Disposable
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxProcessor

@RestController
class MessageController {


  @GetMapping(path = "/messages/{from}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Message> trackMessagesFrom(@PathVariable("from") String from) {

    FluxProcessor<Message, Message> messageEmitter = EmitterProcessor.<Message>create().serialize()

    ReactorNettyWebSocketClient webSocketClient = new ReactorNettyWebSocketClient()

    Disposable disposable = webSocketClient
      .execute(new URI("ws://localhost:8086/messages/${from}"), new MessageFromSubscriber(messageEmitter))
      .subscribe()

    messageEmitter
      .log("MESSAGES EMITTER")
      .doOnCancel({ -> disposable.dispose() })
      .share()
  }
}

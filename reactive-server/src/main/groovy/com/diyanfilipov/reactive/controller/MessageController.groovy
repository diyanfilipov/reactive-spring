package com.diyanfilipov.reactive.controller

import com.diyanfilipov.reactive.dao.Message
import com.diyanfilipov.reactive.model.MessageSentRequest
import com.diyanfilipov.reactive.service.MessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/message")
class MessageController {

  @Autowired
  MessageService messageService

  @PostMapping(value = "/send", consumes = [MediaType.APPLICATION_JSON_VALUE])
  Message sendMessage(@RequestBody MessageSentRequest messageSentRequest) {
    messageService.sendMessage(messageSentRequest)
  }


}

package com.diyanfilipov.reactive.service

import com.diyanfilipov.reactive.config.Profiles
import com.diyanfilipov.reactive.dao.Message
import com.diyanfilipov.reactive.model.MessageSentRequest
import com.diyanfilipov.reactive.repository.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.FluxProcessor

@Service
class MessageService {

  @Autowired(required = false)
  MessageRepository messageRepository

  @Autowired
  FluxProcessor<Message, Message> messageSentPublisher

  @Transactional(readOnly = false)
  Message sendMessage(MessageSentRequest sendMessageRequest) {
    Message message = messageRepository.save(new Message(message: sendMessageRequest.message, fromName: sendMessageRequest.from, toName: sendMessageRequest.to))
    messageSentPublisher.onNext(message)
    message
  }
}

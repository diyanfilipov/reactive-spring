package com.diyanfilipov.reactive.repository

import com.diyanfilipov.reactive.dao.Message
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository extends JpaRepository<Message, String> {

}
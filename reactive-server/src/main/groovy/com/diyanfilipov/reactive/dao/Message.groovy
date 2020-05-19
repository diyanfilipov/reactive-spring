package com.diyanfilipov.reactive.dao


import javax.persistence.Entity

@Entity
class Message extends UuidPersistable {


  String message

  String fromName
  String toName
}

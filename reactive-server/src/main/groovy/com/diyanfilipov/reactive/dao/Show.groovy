package com.diyanfilipov.reactive.dao


import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.MappedSuperclass

@Entity(name = 'shows')
class Show extends UuidPersistable {

  @Column(name = "name", nullable = false)
  String name

  @Column(name = "show_date", nullable = false)
  Date showDate
}

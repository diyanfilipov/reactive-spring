package com.diyanfilipov.reactive.dao

import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener

import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

@MappedSuperclass
@EntityListeners(AuditingEntityListener)
abstract class UuidPersistable implements Persistable<String> {

  @Id
  @Column(name = "ID", nullable = false)
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @GeneratedValue(generator = "system-uuid")
  String id

  @CreatedDate
  Date dateCreated

  @LastModifiedDate
  Date lastUpdated

  @Version
  long version

  @Override
  boolean isNew() {
    id == null
  }
}

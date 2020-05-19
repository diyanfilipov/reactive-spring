package com.diyanfilipov.reactive.dao.rx


import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

import javax.persistence.EntityListeners

@Table
@EntityListeners(AuditingEntityListener)
class RxWallet implements Persistable<Long> {

  @Id
  Long id

  @CreatedDate
  Date dateCreated

  @LastModifiedDate
  Date lastUpdated

  @Version
  long version

  @Column
  String owner

  @Column
  BigDecimal balance

  @Column
  int numWithdrawals

  @Column
  int numDeposits

  boolean hasEnoughFunds(BigDecimal amount) {
    balance >= amount
  }

  void withdraw(BigDecimal amount) {
    if (!hasEnoughFunds(amount)) {
      throw new IllegalStateException("Not enough funds!")
    }
    balance -= amount
    numWithdrawals++
  }

  void deposit(BigDecimal amount) {
    balance += amount
    numDeposits++
  }

  @Override
  boolean isNew() {
    id == null
  }
}

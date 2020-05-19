package com.diyanfilipov.reactive.model

class MoneyTransferRequest implements Serializable {

  String fromAccount
  String toAccount
  BigDecimal amount
}

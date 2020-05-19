package com.diyanfilipov.reactive.client.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class RxWallet {
  String id
  String owner
}

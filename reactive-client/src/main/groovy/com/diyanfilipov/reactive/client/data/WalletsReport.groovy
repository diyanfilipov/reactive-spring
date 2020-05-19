package com.diyanfilipov.reactive.client.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class WalletsReport {
  BigDecimal totalBalance
  int numWithdrawals
  int numDeposits
}

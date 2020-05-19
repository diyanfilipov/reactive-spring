package com.diyanfilipov.reactive.repository.rx


import com.diyanfilipov.reactive.dao.rx.RxWallet
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.r2dbc.repository.query.Query
import reactor.core.publisher.Mono

interface RxWalletRepository extends R2dbcRepository<RxWallet, Long> {

  @Query("SELECT * FROM rx_wallet WHERE owner = :owner")
  Mono<RxWallet> findByOwner(String owner)
}
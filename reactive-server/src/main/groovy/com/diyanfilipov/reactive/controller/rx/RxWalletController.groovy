package com.diyanfilipov.reactive.controller.rx

import com.diyanfilipov.reactive.dao.rx.RxWallet
import com.diyanfilipov.reactive.model.MoneyTransferRequest
import com.diyanfilipov.reactive.model.WalletCreateRequest
import com.diyanfilipov.reactive.service.rx.RxWalletService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping('/wallet')
class RxWalletController {

  @Autowired
  RxWalletService walletService

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<RxWallet> createWalletWithInitialAmount(@RequestBody WalletCreateRequest walletCreateRequest) {
    walletService.createWalletWithInitialAmount(
      Mono.just(walletCreateRequest.owner),
      Mono.just(walletCreateRequest.balance)
    )
  }

  @PostMapping(value = '/transfer', consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<RxWalletService.TxResult> transferMoney(@RequestBody MoneyTransferRequest moneyTransferRequest) {
    walletService.transferMoney(
      Mono.just(moneyTransferRequest.fromAccount),
      Mono.just(moneyTransferRequest.toAccount),
      Mono.just(moneyTransferRequest.amount),
    )
  }

  @GetMapping(value = '/report', produces = MediaType.APPLICATION_JSON_VALUE)
  Mono<RxWalletService.Statistics> walletsReport() {
    walletService.reportAllWallets()
  }

}

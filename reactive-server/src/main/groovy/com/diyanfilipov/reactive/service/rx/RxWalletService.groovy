package com.diyanfilipov.reactive.service.rx

import com.diyanfilipov.reactive.dao.rx.RxWallet
import com.diyanfilipov.reactive.repository.rx.RxWalletRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

import java.time.Duration

@Service
class RxWalletService {

  @Autowired(required = false)
  RxWalletRepository walletRepository

  @Transactional(readOnly = false)
  Mono<RxWallet> createWalletWithInitialAmount(Mono<String> owner, Mono<BigDecimal> amount) {

    Mono.zip(owner, amount)
      .flatMap({ tuple ->
        walletRepository.findByOwner(tuple.getT1())
          .map({ wallet ->
            throw new IllegalArgumentException("Wallet for ${owner} already exists!")
          })

        walletRepository.save(
          new RxWallet(
            owner: tuple.getT1(),
            balance: tuple.getT2()
          )
        )
      })
  }

  @Transactional(readOnly = false)
  Mono<TxResult> transferMoney(Mono<String> fromOwner, Mono<String> toOwner, Mono<BigDecimal> requestAmount) {
    Mono.zip(fromOwner, toOwner, requestAmount)
      .flatMap({ tuple ->
        doTransferMoney(tuple.getT1(), tuple.getT2(), tuple.getT3())
      })
      .retryBackoff(
        20, Duration.ofMillis(1),
        Duration.ofMillis(500), 0.1
      )
      .onErrorReturn(TxResult.TX_CONFLICT)
  }

  private Mono<TxResult> doTransferMoney(String from, String to, BigDecimal amount) {
    walletRepository.findByOwner(from)
      .flatMap({ fromWallet ->
        walletRepository.findByOwner(to)
          .flatMap({ toWallet ->
            if (fromWallet.hasEnoughFunds(amount)) {
              fromWallet.withdraw(amount)
              toWallet.deposit(amount)

              walletRepository.save(fromWallet)
                .then(walletRepository.save(toWallet))
                .then(Mono.just(TxResult.SUCCESS))
            } else {
              Mono.just(TxResult.NOT_ENOUGH_FUNDS)
            }
          })
      })
      .onErrorResume({ e ->
        Mono.error(new RuntimeException('Conflict'))
      })
  }

  Mono<Statistics> reportAllWallets() {
    walletRepository.findAll()
      .reduce(Statistics.init(), { s, w ->
        s.record(w)
        s
      })
  }


  static enum TxResult {
    SUCCESS,
    NOT_ENOUGH_FUNDS,
    TX_CONFLICT
  }

  static class Statistics {
    BigDecimal totalBalance
    int numWithdrawals
    int numDeposits

    static Statistics init() {
      new Statistics(totalBalance: BigDecimal.ZERO, numWithdrawals: 0, numDeposits: 0)
    }

    void record(RxWallet wallet) {
      totalBalance = totalBalance.add(wallet.balance)
      numWithdrawals += wallet.numWithdrawals
      numDeposits += wallet.numDeposits
    }
  }
}

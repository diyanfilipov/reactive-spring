package com.diyanfilipov.reactive.test


import com.diyanfilipov.reactive.config.Profiles
import com.diyanfilipov.reactive.dao.rx.RxWallet
import com.diyanfilipov.reactive.repository.rx.RxWalletRepository
import com.diyanfilipov.reactive.service.rx.RxWalletService
import org.apache.commons.lang3.RandomStringUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import reactor.core.publisher.Flux
import reactor.core.publisher.Hooks
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import java.time.Duration
import java.util.stream.Collectors

@ActiveProfiles(Profiles.R2DBC)
@SpringBootTest
@RunWith(SpringRunner)
class WalletRepositoryTests {

  @Autowired
  RxWalletService walletService

  @Autowired
  RxWalletRepository walletRepository

  List<Long> walletIds

  @Before
  void setUp() {
    walletIds = []
    Hooks.onOperatorDebug()
  }

  @After
  void cleanUp() {

    walletRepository.deleteAll(walletRepository.findAllById(Flux.fromIterable(walletIds)))
      .<StepVerifier.FirstStep>as(StepVerifier.&create)
      .expectSubscription()
      .verifyComplete()
  }

  @Test
  void walletIsSaved() {

    walletService.createWalletWithInitialAmount(
      Mono.just(RandomStringUtils.random(5, true, false)),
      Mono.just(new BigDecimal(1000))
    ).<StepVerifier.FirstStep>as( { w -> StepVerifier.create(w)})
    .expectSubscription()
    .expectNextMatches({ RxWallet w ->
      walletIds << w.id
      w.id != null })
    .verifyComplete()
  }
}

package com.diyanfilipov.reactive.client

import com.diyanfilipov.reactive.client.config.ClientConfig
import com.diyanfilipov.reactive.client.data.MoneyTransferRequest
import com.diyanfilipov.reactive.client.data.RxWallet
import com.diyanfilipov.reactive.client.data.TxResult
import com.diyanfilipov.reactive.client.data.WalletCreateRequest
import com.diyanfilipov.reactive.client.data.WalletsReport
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.RandomStringUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier

import java.time.Duration

@Slf4j
@RunWith(SpringRunner)
@Import(ClientConfig)
//@SpringBootTest
class ReactiveClientApplicationTests {

  private static final int NUM_ITERATIONS = 5
  private static final int NUM_ACCOUNTS = 2
  private static final BigDecimal INITIAL_BALANCE = 1000
  private static final Random random = new Random()

  private static List<String> accountNames
  private static Stats stats

  @Autowired
  WebClient walletClient

  @Before
  void setUp() {
    stats = new Stats()

    // setup accounts
    accountNames = []

    (1..NUM_ACCOUNTS).each {
      walletClient
        .post()
        .contentType(MediaType.APPLICATION_JSON)
        .syncBody(
          new WalletCreateRequest(
            owner: RandomStringUtils.random(7, true, false),
            balance: INITIAL_BALANCE
          )
        )
        .retrieve()
        .bodyToMono(RxWallet)
        .<StepVerifier.FirstStep>as(StepVerifier.&create)
        .expectNextMatches({ RxWallet w ->
          accountNames << w.owner
          w.id != null
        })
        .verifyComplete()
    }
  }

  @After
  void cleanUp() {
  }

  @Test
  void runWalletWithdrawSimulation() {

    log.info("The number of accounts in the system: $NUM_ACCOUNTS")
    log.info("The total balance of the system before the simulation: \$${NUM_ACCOUNTS * INITIAL_BALANCE}")
    log.info("Running the money transferring simulation (${NUM_ITERATIONS} iterations)")


    Flux.range(0, NUM_ITERATIONS)
      .flatMap({ i ->
        Mono.delay(Duration.ofMillis(random.nextInt(10)))
          .publishOn(Schedulers.parallel())
          .flatMap({ ii ->
            String fromOwner = randomOwner()
            String toOwner = randomOwnerExcept(fromOwner)
            BigDecimal amount = randomTransferAmount()

            walletClient
              .post()
              .uri('/transfer')
              .contentType(MediaType.APPLICATION_JSON)
              .syncBody(
                new MoneyTransferRequest(
                  fromAccount: fromOwner,
                  toAccount: toOwner,
                  amount: amount,
                )
              )
              .retrieve()
              .bodyToMono(TxResult)
          })
      })
      .reduce(stats, { a, r ->
        a.record(r)
        a
      })
      .<StepVerifier.FirstStep>as(StepVerifier.&create)
      .expectNextCount(1)
      .verifyComplete()

    log.info("The simulation is finished")
    log.info("""
Transfer operations statistic:
  - successful transfer operations: ${stats.success}
  - not enough funds: ${stats.notEnoughFunds}
  - conflicts: ${stats.conflict}
""")

    walletClient
      .get()
      .uri('/report')
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(WalletsReport)
      .map({ report ->
        log.info("""
All wallet operations:
  - total withdraw operations: ${report.numWithdrawals}
  - total deposit operations: ${report.numDeposits}

The total balance of the system after the simulation: \$ ${report.totalBalance}
""")
        report
      })
      .<StepVerifier.FirstStep>as(StepVerifier.&create)
      .expectNextCount(1)
      .verifyComplete()
  }

  BigDecimal randomTransferAmount() {
    new BigDecimal(new Random().nextFloat() * INITIAL_BALANCE)
  }

  String randomOwnerExcept(String except) {
    String owner = accountNames[new Random().nextInt(accountNames.size())]

    while (owner == except) {
      owner = accountNames[new Random().nextInt(accountNames.size())]
    }

    owner
  }

  String randomOwner() {
    accountNames[new Random().nextInt(accountNames.size())]
  }

  static class Stats {
    int success
    int notEnoughFunds
    int conflict

    void record(TxResult txResult) {
      switch (txResult) {
        case TxResult.SUCCESS:
          success++
          break
        case TxResult.NOT_ENOUGH_FUNDS:
          notEnoughFunds++
          break
        case TxResult.TX_CONFLICT:
          conflict++
      }
    }
  }
}

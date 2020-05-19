package com.diyanfilipov.reactive.config


import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier

@Profile(Profiles.R2DBC_IN_MEMORY)
@Configuration
class R2dbcInMemoryConfig {

  @Bean
  CommandLineRunner initDatabase(ConnectionFactory connectionFactory) {
    { args ->
      DatabaseClient client = DatabaseClient.create(connectionFactory)


      client.execute().sql("""
      create table rx_wallet (
                id bigint not null auto_increment primary key, 
                date_created timestamp not null, 
                last_updated timestamp, 
                version bigint not null, 
                owner varchar(255) not null unique, 
                balance numeric(5))
      """)
        .fetch()
        .rowsUpdated()
        .<StepVerifier.FirstStep>as(StepVerifier.&create)
        .expectNextCount(1)
        .verifyComplete()
    }


//    { args ->
//      Flux.<Connection>from(cf.create())
//        .flatMap({ c ->
//          Flux.<Result>from(c.createBatch()
//            .add("drop table if exists RxWallet")
//            .add("""
//              create table rx_wallet (
//                id bigint not null auto_increment primary key,
//                date_created timestamp not null,
//                last_updated timestamp,
//                version bigint not null,
//                owner varchar(255) not null unique,
//                balance numeric(5))
//            """)
//            .execute())
//            .doFinally({ st -> c.close() })
//        })
//        .log()
//        .blockLast()
//    }
  }
}

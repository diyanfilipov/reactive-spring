package com.diyanfilipov.reactive.config

import com.diyanfilipov.reactive.repository.rx.RxWalletRepository
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Profile([Profiles.BLOCKING_JDBC, Profiles.IN_MEMORY])
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ['com.diyanfilipov.reactive.repository'], excludeFilters = [
  @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [RxWalletRepository])
])
@EnableJpaAuditing(modifyOnCreate = true)
class JpaPersistenceConfig {
}

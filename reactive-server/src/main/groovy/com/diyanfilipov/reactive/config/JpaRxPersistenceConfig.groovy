package com.diyanfilipov.reactive.config


import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Profile([Profiles.R2DBC, Profiles.R2DBC_IN_MEMORY])
@Configuration
@EnableTransactionManagement
@EnableR2dbcRepositories(basePackages = ['com.diyanfilipov.reactive.repository.rx'])
@EnableJpaAuditing(modifyOnCreate = true)
class JpaRxPersistenceConfig {
}

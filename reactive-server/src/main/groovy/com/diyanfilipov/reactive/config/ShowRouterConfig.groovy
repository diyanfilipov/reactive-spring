package com.diyanfilipov.reactive.config

import com.diyanfilipov.reactive.handler.ShowHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse

import static org.springframework.http.HttpMethod.GET
import static org.springframework.web.reactive.function.server.RequestPredicates.*
import static org.springframework.web.reactive.function.server.RouterFunctions.nest
import static org.springframework.web.reactive.function.server.RouterFunctions.route

@Configuration
class ShowRouterConfig {
  @Bean
  @ConditionalOnBean(ShowHandler)
  RouterFunction<ServerResponse> routes(ShowHandler showHandler) {

    nest(path('/router'),
      nest(path('/show'),
        nest(accept(MediaType.APPLICATION_JSON), route(GET('/{id}'), showHandler.&findShowReactive)
          .andRoute(method(GET), showHandler.&findAllShowsReactive)
        )
      ).
        andRoute(method(GET), showHandler.&streamShows)
    )
  }
}

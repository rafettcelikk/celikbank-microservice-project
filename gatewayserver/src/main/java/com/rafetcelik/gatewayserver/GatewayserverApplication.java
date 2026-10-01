package com.rafetcelik.gatewayserver;

import com.rafetcelik.gatewayserver.filters.FilterUtility;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.servlet.function.HandlerFilterFunction;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.addResponseHeader;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.filter.RetryFilterFunctions.retry;

@SpringBootApplication
@EnableDiscoveryClient
@Configuration
public class GatewayserverApplication {

	@Autowired
	private FilterUtility filterUtility;

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouterFunction<ServerResponse> celikBankRouteConfig() {
		return GatewayRouterFunctions.route("accounts-route")
				.route(RequestPredicates.path("/celikbank/accounts/**"), HandlerFunctions.http())
				.filter(lb("accounts"))
				.filter(rewritePath("/celikbank/accounts/(?<segment>.*)", "/${segment}"))
				.filter(addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
				.filter(filterUtility.correlationIdFilter())
				.filter(circuitBreaker("accountsCircuitBreaker", URI.create("forward:/contactSupport")))
				.build()

				.and(GatewayRouterFunctions.route("loans-route")
						.route(RequestPredicates.path("/celikbank/loans/**"), HandlerFunctions.http())
						.filter(lb("loans"))
						.filter(rewritePath("/celikbank/loans/(?<segment>.*)", "/${segment}"))
						.filter(addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.filter(retry(retryConfig -> retryConfig.setRetries(3)
								.setMethods(Set.of(HttpMethod.GET))))
						.filter(filterUtility.correlationIdFilter())
						.build())

				.and(GatewayRouterFunctions.route("cards-route")
						.route(RequestPredicates.path("/celikbank/cards/**"), HandlerFunctions.http())
						.filter(lb("cards"))
						.filter(rewritePath("/celikbank/cards/(?<segment>.*)", "/${segment}"))
						.filter(addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
						.filter(filterUtility.correlationIdFilter())
						.filter(customRateLimiter())
						.build());
	}

	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.timeLimiterConfig(TimeLimiterConfig.custom()
						.timeoutDuration(Duration.ofSeconds(4))
						.build())
				.build());
	}

	private HandlerFilterFunction<ServerResponse, ServerResponse> customRateLimiter() {
		return (request, next) -> {
			String user = request.headers().firstHeader("user");
			user = (user != null) ? user : "anonymous";
			boolean isAllowed = true;

			if (!isAllowed) {
				return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
						.header("X-RateLimit-Remaining", "0")
						.build();
			}

			return next.handle(request);
		};
	}
}
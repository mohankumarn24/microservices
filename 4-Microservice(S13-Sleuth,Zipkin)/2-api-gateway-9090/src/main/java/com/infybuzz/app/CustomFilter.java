package com.infybuzz.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
public class CustomFilter implements GlobalFilter {

	Logger logger = LoggerFactory.getLogger(CustomFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		ServerHttpRequest request = exchange.getRequest();

		// pre-filter 1
		if (request.getURI().toString().contains("/api/student/")) {
			
		}

		// pre-filter 2
		// if authentication is failed we can return error from here itself and no need to send request to student-service
		logger.info(String.format("Authorization = %s", request.getHeaders().getFirst("Authorization")));

		// return chain.filter(exchange);
		// post filter
		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			ServerHttpResponse response = exchange.getResponse();
			
			logger.info("Post Filter = " + response.getStatusCode());
		}));
	}
}
/*
2025-09-08 19:08:19.980  INFO [api-gateway,8487e3ca3e6d655f,8487e3ca3e6d655f] 11100 --- [ctor-http-nio-3] com.infybuzz.app.CustomFilter            : Authorization = ABCD
2025-09-08 19:08:19.993  INFO [api-gateway,8487e3ca3e6d655f,d3e65916b132455a] 11100 --- [ctor-http-nio-8] com.infybuzz.app.CustomFilter            : Authorization = null
2025-09-08 19:08:20.000  INFO [api-gateway,8487e3ca3e6d655f,d3e65916b132455a] 11100 --- [ctor-http-nio-6] com.infybuzz.app.CustomFilter            : Post Filter = 200 OK
2025-09-08 19:08:20.004  INFO [api-gateway,8487e3ca3e6d655f,8487e3ca3e6d655f] 11100 --- [ctor-http-nio-5] com.infybuzz.app.CustomFilter            : Post Filter = 200 OK

Authorization = ABCD		-> 	Request going from api-gateway to student-service
Authorization = null		->	Request going from student-service to address-service
Post Filter = 200 OK		->	Response from address-service to student-service
Post Filter = 200 OK		->	Response from student-service to api-gateway
*/

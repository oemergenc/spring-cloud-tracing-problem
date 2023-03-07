package com.example.tracing.problem.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class RequestIdFilter implements WebFilter {

    private Logger LOG = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        LOG.info("Webfilter start");
        return chain.filter(exchange)
                .doAfterSuccessOrError((r, t) -> LOG.info("Webfilter end"));
    }
}

package com.example.tracing.problem.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.function.Consumer;

@Configuration
public class RequestIdFilter implements WebFilter {

    private Logger LOG = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        return chain.filter(exchange)
                .contextWrite(Context.of(HttpHeaders.class, headers))
                .doAfterSuccessOrError((r, t) -> logWithContext(headers, httpHeaders -> LOG.info("Some message with MDC set")));
    }

    static void logWithContext(HttpHeaders headers, Consumer<HttpHeaders> logAction) {
        try {
            headers.forEach((name, values) -> MDC.put(name, values.get(0)));
            logAction.accept(headers);
        } finally {
            headers.keySet().forEach(MDC::remove);
        }

    }

}

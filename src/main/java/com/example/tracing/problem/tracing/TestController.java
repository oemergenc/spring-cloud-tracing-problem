package com.example.tracing.problem.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/webflux/open")
public class TestController {
  private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

  private final WebClient webClient;

  public TestController(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.build();
  }

  @RequestMapping(method = GET, path = "/echo")
  public Mono<ResponseEntity<String>> user() {
    LOG.info("Got echo request");
    return Mono.just(ResponseEntity.ok("hello"));
  }

  @GetMapping("/forward")
  public Mono<String> forward(@RequestParam("target") String target)
      throws UnsupportedEncodingException {
    LOG.info("Got forward request");
    String decodedUrl = URLDecoder.decode(target, UTF_8.name());
    return webClient.get().uri(decodedUrl).retrieve().bodyToMono(String.class);
  }

  @RequestMapping(method = GET, path = "/filtered1/accesslog")
  public Mono<ResponseEntity<String>> filtered1AccessLog() {
    LOG.info("Got filtered1AccessLog request");
    return Mono.just(ResponseEntity.ok("filtered1AccessLog"));
  }

  @RequestMapping(method = GET, path = "/filtered2/accesslog")
  public Mono<ResponseEntity<String>> filtered2AccessLog() {
    LOG.info("Got filtered2AccessLog request");
    return Mono.just(ResponseEntity.ok("filtered2AccessLog"));
  }
}

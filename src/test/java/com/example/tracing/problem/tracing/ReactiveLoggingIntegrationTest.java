package com.example.tracing.problem.tracing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.BufferedReader;
import java.io.StringReader;
import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(OutputCaptureExtension.class)
class ReactiveLoggingIntegrationTest {
    @Autowired
    WebTestClient client;

    @BeforeEach
    public void setUp() {
        client = client.mutate()
                .responseTimeout(Duration.ofMillis(60000))
                .build();
    }

    @Test
    void app_starts_logging_application_log_as_json(CapturedOutput capturedOutput) {
        String url = "/webflux/open/echo?queryParam1=value1&queryParam2=value2";

        final EntityExchangeResult<String> result =
                client.get().uri(url).exchange().expectBody(String.class).returnResult();

        final List<String> applicationLogLines = extractLogs(capturedOutput, this::isApplicationLog);

        assertThat(result.getStatus().value()).isEqualTo(HttpStatus.OK.value());
        assertThat(applicationLogLines)
                .isNotEmpty()
                .anySatisfy(
                        logLine ->
                                assertThatJson(logLine)
                                        .node("severity")
                                        .isEqualTo("INFO")
                                        .node("log_type")
                                        .isEqualTo("application")
                                        .node("message")
                                        .isEqualTo("Webfilter end")
                                        .node("timestampSeconds")
                                        .isPresent()
                                        .node("timestampNanos")
                                        .isPresent()
                                        .node("thread")
                                        .isPresent()
                                        .node("logger")
                                        .node("logging\\.googleapis\\.com/trace")
                                        .isPresent()
                                        .node("logging\\.googleapis\\.com/spanId")
                                        .isPresent());
    }

    private boolean isApplicationLog(String logLine) {
        return logLine.contains("\"log_type\":\"application");
    }

    private boolean isAccessLog(String logLine) {
        return logLine.contains("\"log_type\":\"access");
    }

    private List<String> extractLogs(CapturedOutput capturedOutput, Predicate<String> p) {
        return await().until(() -> extractOutputFor(capturedOutput, p), logs -> !logs.isEmpty());
    }

    private List<String> extractOutputFor(CapturedOutput output, Predicate<String> p) {
        return new BufferedReader(new StringReader(output.getOut()))
                .lines()
                .filter(s -> s.startsWith("{") && p.test(s))
                .collect(Collectors.toList());
    }
}

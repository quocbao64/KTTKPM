package com.example.orderservice;

import com.example.orderservice.dto.OrderRequestDTO;
import com.example.orderservice.dto.OrderResponseDTO;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.swagger.models.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockserver.model.StringBody.exact;

@SpringBootTest
@ExtendWith(MockServerExtension.class)
@Slf4j
class OrderServiceApplicationTests {
    private final ClientAndServer clientAndServer;
    private final RestTemplate restTemplate;
    public OrderServiceApplicationTests(ClientAndServer clientAndServer) {
        this.clientAndServer = clientAndServer;
        this.restTemplate = new RestTemplateBuilder()
                .rootUri("http://localhost:" + this.clientAndServer.getPort())
                .build();
    }

    @AfterEach
    public void reset() {
        this.clientAndServer.reset();
    }

    @Test
    public void basicConfig_nothingHappensIfSlidingWindowNotFilled() {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1);
        request.setProductId(2);

        HttpRequest expectedFirstRequest = HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withPath("/api/v1/orders")
                .withBody(new JsonBody("{\"userId\": 1, \"productId\": 2}", StandardCharsets.UTF_8, MatchType.STRICT));

        this.clientAndServer
                .when(expectedFirstRequest)
                .respond(HttpResponse.response().withStatusCode(500));

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .slidingWindowSize(10)
                .build();
        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);
        CircuitBreaker callingEndpointCircuitBreaker = circuitBreakerRegistry.circuitBreaker("call-endpoint");
        for (int i = 1; i < 11; i++) {
            try {
                callingEndpointCircuitBreaker.decorateSupplier(() ->
                        restTemplate.postForEntity("/api/v1/orders", request, OrderResponseDTO.class)
                ).get();
                fail("we should never get here!");
            } catch (HttpServerErrorException e) {
                log.error(e.getMessage());
            }
        }

        try {
            callingEndpointCircuitBreaker.decorateSupplier(() ->
                    restTemplate.postForEntity("/api/v1/orders", request, OrderResponseDTO.class)
            ).get();
            fail("we should never get here!");
        } catch (CallNotPermittedException callNotPermittedException)  {
            assertEquals("call-endpoint", callNotPermittedException.getCausingCircuitBreakerName());
            assertSame(CircuitBreaker.State.OPEN, callingEndpointCircuitBreaker.getState());
        }
    }

    @Test
    public void clientErrorException_stillTripsTheCircuit() {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1);
        request.setProductId(2);
        HttpRequest expectedFirstRequest = HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withPath("/api/v1/orders")
                .withBody(new JsonBody("{\"userId\": 1, \"productId\": 2}", StandardCharsets.UTF_8, MatchType.STRICT));

        this.clientAndServer
                .when(expectedFirstRequest)
                .respond(HttpResponse.response().withStatusCode(404));

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .slidingWindowSize(10)
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);

        CircuitBreaker callingEndpointCircuitBreaker = circuitBreakerRegistry.circuitBreaker("call-endpoint");

        // force the circuit to trip
        for (int i = 1; i < 11; i++) {
            try {
                callingEndpointCircuitBreaker.decorateSupplier(() ->
                        restTemplate.postForEntity("/api/v1/orders", request, OrderResponseDTO.class)
                ).get();
                fail("we should never get here!");
            } catch (HttpClientErrorException e) {
                // expected
            }
        }

        // circuit is now tripped, but should it be?
        try {
            callingEndpointCircuitBreaker.decorateSupplier(() ->
                    restTemplate.postForEntity("/api/v1/orders", request, OrderResponseDTO.class)
            ).get();
            fail("we should never get here!");
        } catch (CallNotPermittedException callNotPermittedException)  {
            assertEquals("call-endpoint", callNotPermittedException.getCausingCircuitBreakerName());
            assertSame(CircuitBreaker.State.OPEN, callingEndpointCircuitBreaker.getState());
        }
    }

    @Test
    public void excludingClientErrorExceptions_fromTheCount() {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(1);
        request.setProductId(2);
        HttpRequest expectedFirstRequest = HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withPath("/api/v1/orders")
                .withBody(new JsonBody("{\"userId\": 1, \"productId\": 2}", StandardCharsets.UTF_8, MatchType.STRICT));

        this.clientAndServer
                .when(expectedFirstRequest)
                .respond(HttpResponse.response().withStatusCode(404));

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
                .custom()
                .ignoreException(throwable -> throwable instanceof HttpClientErrorException)
                .slidingWindowSize(10)
                .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
                CircuitBreakerRegistry.of(circuitBreakerConfig);

        CircuitBreaker callingEndpointCircuitBreaker = circuitBreakerRegistry.circuitBreaker("call-endpoint");

        // before we ignored the exception above, this would trip the circuit
        for (int i = 1; i < 11; i++) {
            try {
                callingEndpointCircuitBreaker.decorateSupplier(() ->
                        restTemplate.postForEntity("/api/v1/orders", request, OrderResponseDTO.class)
                ).get();
                fail("we should never get here!");
            } catch (HttpClientErrorException e) {
                // expected
            }
        }

        // the circuit doesn't trip
        try {
            callingEndpointCircuitBreaker.decorateSupplier(() ->
                    restTemplate.postForEntity("/api/v1/orders", request, OrderResponseDTO.class)
            ).get();
            fail("we should never get here!");
        } catch (HttpClientErrorException httpClientErrorException)  {
            assertEquals(HttpStatus.NOT_FOUND, httpClientErrorException.getStatusCode());
            assertSame(CircuitBreaker.State.CLOSED, callingEndpointCircuitBreaker.getState());
        }
    }
}

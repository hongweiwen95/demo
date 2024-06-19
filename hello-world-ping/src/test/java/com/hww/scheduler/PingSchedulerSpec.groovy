package com.hww.scheduler

import org.mockito.Mock
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PingSchedulerSpec extends Specification {


    @Shared
    @AutoCleanup
    @Mock
    WebClient webClient;

    PingScheduler pingScheduler

    def setup() {
        pingScheduler = new PingScheduler("http://localhost:8081")
        pingScheduler.webClient = webClient
    }

    def "test pingPongService success"() {
        given:
        def responseBody = "pong"
        webClient.get().uri("http://localhost:8081").exchange() >> Mono.just(responseBody)

        when:
        pingScheduler.pingPongService()

        then:
        1 * webClient.get().uri("http://localhost:8081/ping").exchange() >> {

            delegate.bodyToMono(String).block() == responseBody
        }
    }

    def "test pingPongService error handling IOException"() {
        given:
        def ioException = new IOException("Connection refused")
        webClient.get().uri("http://localhost:8081/ping ").exchange() >> Mono.error(ioException);

        when:
        pingScheduler.pingPongService()

        then:
        1 * webClient.get().uri("http://localhost:8081/ping").exchange() >> { // verify the request
            delegate.bodyToMono(String).block() == null
        }
        pingScheduler.logToFile("REQUEST NOT SENT: Connection refused") >> 1
    }

    def "test pingPongService error handling other Throwable"() {
        given:
        def throttlingException = new RuntimeException("Request throttled")
        webClient.get().uri("http://localhost:8081/ping").exchange() >> Mono.error(throttlingException)

        when:
        pingScheduler.pingPongService()

        then:
        1 * webClient.get().uri("http://localhost:8081/ping").exchange() >> {
            delegate.bodyToMono(String).block() == null
        }
        pingScheduler.logToFile("REQUEST THROTTLED: Request throttled") >> 1
    }
}


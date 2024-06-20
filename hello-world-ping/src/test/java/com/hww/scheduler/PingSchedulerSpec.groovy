package com.hww.scheduler


import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import spock.lang.Specification

class PingSchedulerSpec extends Specification {


    WebClient webClient = Mock(WebClient)

    PingScheduler pingScheduler

    def setup() {
        pingScheduler = new PingScheduler("http://localhost:8081")
    }

    def "test pingPongService success"() {
        given:
        def responseBody = Mono.just("World")
//        webClient.get().uri("http://localhost:8081/ping").retrieve().bodyToMono(String.class) >> Mono.just(responseBody)

        when:
        pingScheduler.pingPongService()

        then:
        1 * webClient.get()
    }

    def "test pingPongService error handling IOException"() {
        given:
        def ioException = new IOException("Connection refused")
//        webClient.get().uri("http://localhost:8081/ping ").exchange() >> Mono.error(ioException);

        when:
        pingScheduler.pingPongService()

        then:
        1 * webClient.get()
//                .uri("http://localhost:8081/ping").exchange() >> {
//            delegate.bodyToMono(String).block() == null
//        }
        pingScheduler.logToFile("REQUEST NOT SENT: Connection refused") >> 1
    }

    def "test pingPongService error handling other Throwable"() {
        given:
        def throttlingException = new RuntimeException("Request throttled")
//        webClient.get().uri("http://localhost:8081/ping").exchange() >> Mono.error(throttlingException)

        when:
        pingScheduler.pingPongService()

        then:
        1 * webClient.get()
//                .uri("http://localhost:8081/ping").exchange() >> {
//            delegate.bodyToMono(String).block() == null
//        }
        pingScheduler.logToFile("REQUEST THROTTLED: Request throttled") >> 1
    }
}


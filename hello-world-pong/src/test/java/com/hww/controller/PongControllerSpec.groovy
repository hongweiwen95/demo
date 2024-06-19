package com.hww.controller

import com.hww.exception.CustomException
import com.hww.pojo.RateLimiter
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @Author hww* @Date 2024/6/19
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class PongControllerSpec extends Specification {


    RateLimiter rateLimiter = Mock(RateLimiter)

    PongController pongController

    def setup() {
        pongController = new PongController()
        pongController.rateLimiter = rateLimiter
    }

    def "test getPing success"() {
        given:
        rateLimiter.tryAcquire() >> true

        when:
        def result = pongController.getPing()

        then:
        result == "World"
        1 * rateLimiter.tryAcquire()
    }

    def "test getPing too many requests error handling"() {
        given:
        rateLimiter.tryAcquire() >> false

        when:
        def result = pongController.getPing()

        then:
        result.onError { ex ->
            ex instanceof CustomException
            ex.status == 402
            ex.message == "Too many requests"
        }
        1 * rateLimiter.tryAcquire()
    }
}

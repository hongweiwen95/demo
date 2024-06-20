package com.hww.controller


import com.hww.pojo.RateLimiter
import reactor.core.publisher.Mono
import spock.lang.Specification

/**
 * @Author hww* @Date 2024/6/19
 */
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
        def responseBody = Mono.just("World")

        when:
        def result = pongController.getPing()

        then:
        1 * rateLimiter.tryAcquire()
    }

    def "test getPing too many requests error handling"() {
        given:
        rateLimiter.tryAcquire() >> false

        when:
        def result = pongController.getPing()

        then:
        1 * rateLimiter.tryAcquire()
    }
}

package bamboohr.slack.bot

import ai.grakn.redismock.RedisServer
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class BotControllerSpec extends Specification {

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer, ["bamboohr.company": "test", "slack.token": "asd", "slack.teamid": "yxc"]) as EmbeddedServer

    @Shared
    @AutoCleanup
    RxHttpClient client = embeddedServer.applicationContext.createBean(RxHttpClient, embeddedServer.getURL())

    @Shared
    RedisServer redisServer

    void setup() {
        redisServer = RedisServer.newRedisServer(6379)
        redisServer.start()
    }

    void cleanup() {
        redisServer.stop()
    }

    void "test index"() {
        given:
        HttpResponse response = client.toBlocking().exchange("/")

        expect:
        response.status == HttpStatus.OK
    }

    void "login command is working as expected"() {
        given:
        def cmd = [
                "channel_id"     : "DDKH3CRLH",
                "channel_name"   : "directmessage",
                "command"        : "/bamboo",
                "enterprise_id"  : null,
                "enterprise_name": null,
                "parsed"         : true,
                "response_url"   : "https://hooks.slack.com/commands/ASDQ/123/YXC",
                "team_domain"    : "testteam",
                "team_id"        : "T0518UXBP",
                "text"           : "login this-is-a-fake-api-key",
                "trigger_id"     : "498466665013.5042983397.0302fd40cce236bee8c4374e5b210d16",
                "user_id"        : "U054WHS9W",
                "user_name"      : "peh",
                "zoken"          : null
        ]

        when:
        HttpResponse response = client.toBlocking().exchange(
                HttpRequest.POST("/", cmd)
                        .headers([
                        "Content-Type": "application/json; charset=UTF-8",
                ])
        )

        then:
        response.status == HttpStatus.OK
    }

    void "preview command is working as expected"() {
        given:
        def cmd = [
                user_name        : "peh",
                "enterprise_name": null,
                "team_domain"    : "testteam",
                "channel_id"     : "CDKBSF126",
                "zoken"          : null,
                "user_id"        : "U054WHS9W",
                "command"        : "/bamboo",
                "text"           : "preview",
                "enterprise_id"  : null,
                "trigger_id"     : "535706810467.5042983397.70ec5fae94e6f6bb273fb6101df6dcba",
                "response_url"   : "https://hooks.slack.com/commands/ASDQ/123/YXC",
                "channel_name"   : "bbot-test",
                "team_id"        : "T0518UXBP"
        ]
        when:
        HttpResponse response = client.toBlocking().exchange(
                HttpRequest.POST("/", cmd)
                        .headers([
                        "Content-Type": "application/json; charset=UTF-8",
                ])
        )

        then:
        response.status == HttpStatus.OK

    }

}

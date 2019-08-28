package uberall.services

import uberall.data.UserDatabase
import uberall.model.Channel
import uberall.model.EmployeeTimeOffInfo
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class SlackService {

    @Value('${slack.token}')
    String slackToken

    @Client('https://slack.com')
    @Inject
    RxHttpClient client

    @Client('https://hooks.slack.com')
    @Inject
    RxHttpClient hookClient

    @Inject
    UserDatabase userDatabase

    @Inject
    BambooService bambooService

    void post(Channel channel, Map message) {
        message.channel = channel.id
        try {
            def result = client.toBlocking().retrieve(buildRequest("/api/chat.postMessage", message), Map.class)
            if (!result.ok) {
                log.error(result.error)
            }
        } catch (e) {
            log.error "Posting message to slack failed. Message: $message", e
        }
    }

    void reply(String url, Map message) {
        try {
            hookClient.toBlocking().retrieve(buildRequest(new URL(url).path, message), String.class)
        } catch (e) {
            log.error "reply failed", e
        }
    }

    Map<CharSequence, CharSequence> getDefaultHeaders() {
        [
                "Content-Type" : "application/json; charset=UTF-8",
                "Authorization": "Bearer $slackToken"
        ]
    }

    private HttpRequest buildRequest(String uri, def body) {
        HttpRequest.POST(uri, body).headers(defaultHeaders)
    }

    static List buildAttachment(List<EmployeeTimeOffInfo> infos) {
        [
                [
                        color  : "#36a64f",
                        pretext: infos ? "Who's out today?" : "Nobody is out today. :oh-yeah:",
                        footer : "brought to you by the mighty bamboobot",
                        fields : infos*.slackAttachment
                ]
        ]
    }

}

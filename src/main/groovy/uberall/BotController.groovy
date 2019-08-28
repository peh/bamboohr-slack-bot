package uberall

import uberall.data.ChannelDatabase
import uberall.data.UserDatabase
import uberall.model.slack.BotCommand
import uberall.model.slack.BotCommandFactory
import uberall.model.slack.ParseCommandException
import uberall.model.slack.RawSlackCommand
import uberall.services.BambooService
import uberall.services.BotService
import uberall.services.SlackService
import groovy.util.logging.Slf4j
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.reactivex.Single

import javax.inject.Inject

@Controller("/")
@Slf4j
@SuppressWarnings("unused")
class BotController {

    @Inject
    BambooService bambooService

    @Inject
    SlackService slackService

    @Inject
    UserDatabase userDatabase

    @Inject
    ChannelDatabase channelDatabase

    @Inject
    BotService botService

    @Inject
    PostToSlackJob postToSlackJob

    @Get("/")
    String index() {
        return "OK"
    }

    @Get("/test")
    String test() {
        postToSlackJob.process()
        return "OK"
    }

    @Post(value = "/", consumes = [MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON])
    Single<HttpResponse<String>> login(@Body RawSlackCommand cmd) {
        try {
            BotCommand command = BotCommandFactory.parse(cmd)
            return Single.fromCallable({ return HttpResponse.ok("") }).doAfterSuccess({
                botService.handle(command)
            })
        } catch (ParseCommandException e) {
            return Single.fromCallable({
                return HttpResponse.ok([
                        "fallback"   : "`$e.command` is not a valid command. Use `/bamboo help` to get a list of all commands".toString(),
                        "attachments": [
                                [
                                        "color": "#dc143c",
                                        "text" : "`$e.command` is not a valid command. Use `/bamboo help` to get a list of all commands".toString()
                                ]
                        ]
                ])
            })
        }
    }

}

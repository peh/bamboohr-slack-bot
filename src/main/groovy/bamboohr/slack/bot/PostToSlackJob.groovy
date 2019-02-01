package bamboohr.slack.bot


import bamboohr.slack.bot.services.BotService
import io.micronaut.scheduling.annotation.Scheduled

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostToSlackJob {

    @Inject
    BotService botService

    @Scheduled(cron = "0 0 9 ? * MON,TUE,WED,THU,FRI")
    void process() {
        botService.post()
    }
}

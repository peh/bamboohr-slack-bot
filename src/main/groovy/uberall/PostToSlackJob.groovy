package uberall

import uberall.services.BotService
import groovy.util.logging.Slf4j
import io.micronaut.scheduling.annotation.Scheduled

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Slf4j
class PostToSlackJob {

    @Inject
    BotService botService

    @Scheduled(cron = '${bamboobot.job.cron}')
    void process() {
        log.info("processing")
        botService.post()
    }
}

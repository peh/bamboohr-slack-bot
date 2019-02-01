package bamboohr.slack.bot


import bamboohr.slack.bot.model.slack.BotCommandFactory
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Value
import io.micronaut.runtime.Micronaut
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.runtime.server.event.ServerStartupEvent

import javax.inject.Singleton

@Singleton
@Slf4j
@CompileStatic
class Application {

    @Value('${bamboohr.company}')
    String bambooCompany

    @EventListener
    void onStartup(ServerStartupEvent event) {
        BotCommandFactory.init()
        log.info("Application started for $bambooCompany")
    }

    static void main(String[] args) {
        Micronaut.run(Application)
    }
}

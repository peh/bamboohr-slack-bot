package bamboohr.slack.bot

import bamboohr.slack.bot.model.slack.BotCommandFactory
import groovy.transform.CompileStatic
import io.micronaut.runtime.Micronaut

import javax.inject.Singleton

@Singleton
@CompileStatic
class Application {

    static void main(String[] args) {
        BotCommandFactory.init()
        Micronaut.run(Application)
    }
}

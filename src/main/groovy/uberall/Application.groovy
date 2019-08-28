package uberall

import io.micronaut.runtime.Micronaut
import groovy.transform.CompileStatic
import uberall.model.slack.BotCommandFactory

@CompileStatic
class Application {
    static void main(String[] args) {
        BotCommandFactory.init()
        Micronaut.run(Application)
    }
}

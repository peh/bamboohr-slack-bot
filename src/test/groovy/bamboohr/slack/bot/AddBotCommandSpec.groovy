package bamboohr.slack.bot

import bamboohr.slack.bot.model.slack.AddBotCommand
import spock.lang.Specification

class AddBotCommandSpec extends Specification {

    void "init() should extract the channelId properly"() {
        given:
        AddBotCommand cmd = new AddBotCommand()
        cmd.rawCommands = ["<#C0763TJE7|devops>"]

        when:
        cmd.init()

        then:
        cmd.channel == "C0763TJE7"
    }

}

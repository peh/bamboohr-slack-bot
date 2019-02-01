package bamboohr.slack.bot.model.slack

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.reflections.Reflections

@Slf4j
@CompileStatic
class BotCommandFactory {

    private static final List<BotCommand> COMMANDS = []

    static BotCommand parse(RawSlackCommand slackCommand) {
        List<String> rawCommands = slackCommand.text.split(" ") as List
        String cmd = rawCommands.remove(0)
        BotCommand result = createByCommand(cmd)
        result.replyUrl = slackCommand.response_url
        result.slackCommand = slackCommand
        result.rawCommands = rawCommands
        result.init()
        result
    }

    static init() {
        Reflections reflections = new Reflections("bamboohr.slack.bot.model.slack")
        reflections.getSubTypesOf(BotCommand.class).each { cmd ->
            BotCommand c = cmd.newInstance()
            COMMANDS.add(c)
            log.info "${cmd.simpleName} registered with ${c.command}"
        }
        COMMANDS.sort(true) { it.command }
    }

    static BotCommand createByCommand(String command) {
        BotCommand cmd = all.find { it.command == command }
        if (!cmd) {
            throw new ParseCommandException(command)
        }

        cmd.class.newInstance() as BotCommand
    }

    static List<BotCommand> getAll() {
        if (COMMANDS.empty)
            init()
        COMMANDS
    }
}

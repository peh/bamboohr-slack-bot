package uberall.model.slack

abstract class BotCommand {

    RawSlackCommand slackCommand
    List<String> rawCommands
    String replyMessage
    List attachments
    String replyUrl

    abstract void init()

    abstract String getHelpText()

    abstract String getCommand()

    String getUser() {
        slackCommand.user_id
    }

    Map getHelp() {
        [
                title: command,
                value: helpText,
                short: false
        ]
    }
}

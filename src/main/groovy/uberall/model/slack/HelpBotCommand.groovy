package uberall.model.slack

class HelpBotCommand extends BotCommand {

    private static final String HELP_TEXT = "this message"
    private static final String COMMAND = "help"

    @Override
    void init() {
        this.attachments = [
                [
                        pretext: 'Available commands',
                        footer : 'brought to you by the mighty bamboobot',
                        fields : BotCommandFactory.all.collect { it.help }
                ]
        ]
    }

    @Override
    String getHelpText() {
        return HELP_TEXT
    }

    @Override
    String getCommand() {
        return COMMAND
    }
}

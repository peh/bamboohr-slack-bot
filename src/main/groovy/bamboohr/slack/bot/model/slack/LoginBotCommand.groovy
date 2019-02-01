package bamboohr.slack.bot.model.slack

class LoginBotCommand extends BotCommand {

    String apiKey

    private static final String COMMAND = 'login'
    private static final String HELP = "example: `login the-key-you-get-from-bamoboo`\nLogin with your BambooHR API key\nYou can create a new API key in the top-right corner menu in BambooHR"

    @Override
    void init() {
        this.apiKey = rawCommands.remove(0)
    }

    @Override
    String getHelpText() {
        HELP
    }

    @Override
    String getCommand() {
        COMMAND
    }
}

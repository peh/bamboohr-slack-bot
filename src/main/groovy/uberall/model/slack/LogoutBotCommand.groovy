package uberall.model.slack

class LogoutBotCommand extends BotCommand {

    private static final String COMMAND = 'logout'
    private static final String HELP = "Removes you and your API key from my database. This will also disable all channel integrations!"

    @Override
    String getHelpText() {
        HELP
    }

    @Override
    String getCommand() {
        COMMAND
    }

    @Override
    void init() {
        // NO-OP
    }
}

package uberall.model.slack

class AddBotCommand extends BotCommand {

    String channel

    private static final List<String> CLEANUP = ["<", ">", "#"]

    private static final String COMMAND = "add"
    private static final String HELP = "example: `add #hackers`\nadd me to a channel.\nTo see what it will looks like use the preview command"

    @Override
    void init() {
        String channel = rawCommands.remove(0)
        CLEANUP.each {
            channel = channel.replace(it, '')
        }
        this.channel = channel.split("\\|").first()
    }

    @Override
    String getHelpText() {
        return HELP
    }

    @Override
    String getCommand() {
        return COMMAND
    }
}

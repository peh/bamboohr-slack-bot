package bamboohr.slack.bot.model.slack

class ParseCommandException extends RuntimeException {
    String command

    ParseCommandException(String command) {
        this.command = command
    }
}

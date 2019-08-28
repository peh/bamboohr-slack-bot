package uberall.model.slack

import uberall.model.EmployeeTimeOffInfo

class PreviewBotCommand extends BotCommand {

    private static final String COMMAND = 'preview'
    private static final String HELP = "Creates a preview of what i will post, at 9am Berlin time, in any channel you add me to"

    List<EmployeeTimeOffInfo> infos


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

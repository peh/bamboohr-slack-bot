package bamboohr.slack.bot.services

import bamboohr.slack.bot.data.ChannelDatabase
import bamboohr.slack.bot.data.UserDatabase
import bamboohr.slack.bot.model.Channel
import bamboohr.slack.bot.model.EmployeeTimeOffInfo
import bamboohr.slack.bot.model.User
import bamboohr.slack.bot.model.slack.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@CompileStatic
@Slf4j
class BotService {

    @Inject
    BambooService bambooService

    @Inject
    SlackService slackService

    @Inject
    UserDatabase userDatabase

    @Inject
    ChannelDatabase channelDatabase

    /**
     * This is the method called once a day by the Job
     */
    void post() {
        channelDatabase.list.each { Channel channel ->
            log.info("posting to $channel.id for $channel.userId")
            doPost(channel)
        }
    }

    private void doPost(Channel channel) {
        User user = userDatabase.get(channel.userId)
        if (!user) {
            channelDatabase.remove(channel.id)
            return
        }

        List<EmployeeTimeOffInfo> list = bambooService.getEmployeesWhoAreOutToday(user.apiKey)
        def attachment = SlackService.buildAttachment(list)
        def message = [
                attachments: attachment
        ]

        slackService.post(channel, message)
    }

    /**
     * Handles the given BotCommand
     * @param cmd
     */
    void handle(BotCommand cmd) {
        boolean success
        if (cmd instanceof LoginBotCommand) {
            success = handleLoginCommand(cmd)
        } else if (cmd instanceof PreviewBotCommand) {
            success = handlePreviewBotCommand(cmd)
        } else if (cmd instanceof AddBotCommand) {
            success = handleAddBotCommand(cmd)
        } else if (cmd instanceof LogoutBotCommand) {
            success = handleLogoutCommand(cmd)
        } else {
            log.error "${cmd.class} cannot be handled"
            success = false
        }
        List attachments = cmd.attachments
        if (!attachments) {
            attachments = [
                    [
                            "color": success ? "#36a64f" : "#dc143c",
                            "text" : cmd.replyMessage
                    ]
            ]
        }
        slackService.reply(cmd.replyUrl, ["fallback": cmd.replyMessage, "attachments": attachments])
    }

    private boolean handleAddBotCommand(AddBotCommand cmd) {
        Channel channel = new Channel(id: cmd.channel, userId: cmd.user)
        channelDatabase.save(channel)
        cmd.replyMessage = "Saved it. See you there at 9am"
        return true
    }

    private boolean handlePreviewBotCommand(PreviewBotCommand cmd) {
        User user = userDatabase.get(cmd.user)
        if (!user) {
            cmd.replyMessage = "You have not logged in yet. Please Login first with `/bamboo login {apiKey}`"
            return false
        }
        cmd.attachments = SlackService.buildAttachment(bambooService.getEmployeesWhoAreOutToday(user.apiKey))
        return true
    }

    private boolean handleLoginCommand(LoginBotCommand cmd) {
        bambooService.login(cmd)
    }

    private boolean handleLogoutCommand(LogoutBotCommand cmd) {
        bambooService.logout(cmd)
    }
}

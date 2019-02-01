package bamboohr.slack.bot.data

import bamboohr.slack.bot.model.Channel

import javax.inject.Singleton

@Singleton
class ChannelDatabase implements Database<Channel> {

    void save(Channel channel) {
        save(channel.id, channel)
    }
}

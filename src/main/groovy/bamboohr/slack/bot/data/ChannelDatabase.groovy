package bamboohr.slack.bot.data

import bamboohr.slack.bot.model.Channel

import javax.inject.Singleton

@Singleton
class ChannelDatabase extends FileDatabase<Channel> {


    void save(Channel channel) {
        save(channel.id, channel)
    }

    @Override
    Channel newEntity() {
        new Channel()
    }

}

package bamboohr.slack.bot.model

interface RedisEntity<D> {

    abstract D fromJSON(Map json)
}

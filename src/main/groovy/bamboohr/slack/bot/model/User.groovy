package bamboohr.slack.bot.model

class User implements RedisEntity {

    /**
     * we blindly take the slack user id
     */
    String id

    /**
     * bamboo api key
     */
    String apiKey

    /**
     * Name in bamboo
     */
    String name

    @Override
    User fromJSON(Map json) {
        this.id = json.id
        this.apiKey = json.apiKey
        this.name = json.name
        this
    }
}

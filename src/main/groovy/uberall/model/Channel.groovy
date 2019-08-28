package uberall.model

class Channel implements RedisEntity {

    String id

    /**
     * the user who requested, needed to get data
     */
    String userId

    @Override
    Channel fromJSON(Map json) {
        this.id = json.id
        this.userId = json.userId
        this
    }
}

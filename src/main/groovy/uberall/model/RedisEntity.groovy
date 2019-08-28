package uberall.model

interface RedisEntity<D> {

    abstract D fromJSON(Map json)
}

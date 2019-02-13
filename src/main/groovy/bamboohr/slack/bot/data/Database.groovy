package bamboohr.slack.bot.data

import bamboohr.slack.bot.model.RedisEntity
import bamboohr.slack.bot.services.RedisService
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
@CompileStatic
abstract class Database<T extends RedisEntity> {

    private static final Map<String, Map<?, T>> CACHE = new ConcurrentHashMap<>()
    private static final JsonSlurper JSON = new JsonSlurper()
    private static final Logger LOG = LoggerFactory.getLogger(Database.simpleName)

    @Inject
    RedisService redisService

    @PostConstruct
    void init() {
        String fromDB = redisService.get(dbKey)
        Map cache = new ConcurrentHashMap<?, T>()
        if (fromDB) {
            Map<String, Map> m = JSON.parseText(fromDB) as Map<String, Map>
            m.each { String key, Map val ->
                try {
                    T t = newEntity()
                    cache.put(key, t.fromJSON(val))
                    cache.values().each {
                        LOG.info(it.class.simpleName)
                    }
                } catch (e) {
                    LOG.warn("invalid value in the ${dbKey} for '$key'", e)
                }
            }
        }
        CACHE.put(dbKey, cache)
        LOG.info "$dbKey initialized with ${cache.size()} items"
    }

    private Map<?, T> getCache() {
        CACHE.get(dbKey)
    }

    List<T> getList() {
        new ArrayList<T>(cache.values())
    }

    void persist() {
        redisService.set(dbKey, JsonOutput.toJson(cache))
    }

    void save(String key, def value) {
        cache.put(key, value)
        persist()
    }

    void remove(String key) {
        cache.remove(key)
        persist()
    }

    T get(String key) {
        cache.get(key)
    }

    String getDbKey() {
        this.class.simpleName
    }


    abstract T newEntity()
}

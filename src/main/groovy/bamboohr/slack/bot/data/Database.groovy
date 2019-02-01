package bamboohr.slack.bot.data

import bamboohr.slack.bot.services.RedisService
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.micronaut.context.annotation.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.PostConstruct
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
trait Database<T> {

    private static Logger LOG

    @Value('${micronaut.application.name}')
    String applicationName

    private static final Map<String, T> CACHE = new ConcurrentHashMap<String, T>()
    private static final JsonSlurper JSON = new JsonSlurper()

    private String dbKey

    @Inject
    RedisService redisService

    @PostConstruct
    void init() {
        dbKey = "${applicationName}:${this.class.simpleName}"
        LOG = LoggerFactory.getLogger("${Database.class.name}:${this.class.simpleName}")
        String fromDB = redisService.get(dbKey)
        if (fromDB) {
            Map<String, Object> m = JSON.parseText(fromDB)
            m.each { key, val ->
                try {
                    // cast all values
                    CACHE.put(key, val as T)
                } catch (e) {
                    LOG.warn("invalid value in the $dbKey for '$key'")
                }
            }
        }
        LOG.info "$dbKey initialized with ${CACHE.size()} items"
    }

    List<T> getList() {
        new ArrayList<T>(CACHE.values())
    }

    Set<String> getIds() {
        CACHE.keySet()
    }

    void persist() {
        redisService.set(dbKey, JsonOutput.toJson(CACHE))
    }

    void save(String key, def value) {
        CACHE.put(key, value)
        persist()
    }

    void remove(String key) {
        CACHE.remove(key)
        persist()
    }

    T get(String key) {
        CACHE.get(key)
    }

}

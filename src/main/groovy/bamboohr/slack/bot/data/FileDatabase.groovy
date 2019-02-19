package bamboohr.slack.bot.data

import bamboohr.slack.bot.model.RedisEntity
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import io.micronaut.context.annotation.Value
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.PostConstruct
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

@Singleton
@CompileStatic
abstract class FileDatabase<T extends RedisEntity> {

    private static final Map<String, Map<?, T>> CACHE = new ConcurrentHashMap<>()
    private static final JsonSlurper JSON = new JsonSlurper()
    private static final Logger LOG = LoggerFactory.getLogger(FileDatabase.simpleName)

    private static File DB_FILE

    @Value('${db.file:./database.json}')
    String databaseLocation

    @PostConstruct
    void init() {
        if (!DB_FILE) {
            DB_FILE = new File(databaseLocation)
            if (!DB_FILE.exists()) {
                DB_FILE.createNewFile()
                DB_FILE.write('{}', 'UTF-8')
            }
        }
        Map<String, Map> m = JSON.parse(DB_FILE, "UTF-8") as Map<String, Map>
        Map cache = new ConcurrentHashMap<?, T>()
        if (m.containsKey(dbKey)) {
            Map<String, Map> dbData = m.get(dbKey) as Map<String, Map>
            dbData.each { String key, Map val ->
                try {
                    T t = newEntity()
                    cache.put(key, t.fromJSON(val))
                } catch (e) {
                    LOG.warn("invalid value in the ${dbKey} for '$key'", e)
                }
            }
        }
        LOG.info "$dbKey initialized with ${cache.size()} items"
        CACHE.put(dbKey, cache)
    }

    private Map<?, T> getCache() {
        CACHE.get(dbKey)
    }

    List<T> getList() {
        new ArrayList<T>(cache.values())
    }

    static void persist() {
        synchronized (CACHE) {
            DB_FILE.write(JsonOutput.toJson(CACHE), "UTF-8")
        }
    }

    void save(String key, def value) {
        cache.put(key, value)
        Thread.start {
            persist()
        }
    }

    void remove(String key) {
        cache.remove(key)
        Thread.start {
            persist()
        }
    }

    T get(String key) {
        cache.get(key)
    }

    String getDbKey() {
        this.class.simpleName
    }

    abstract T newEntity()
}

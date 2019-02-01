package bamboohr.slack.bot.services

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.micronaut.context.annotation.Value
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisConnectionException

import javax.annotation.PostConstruct
import javax.inject.Singleton

@Singleton
@Slf4j
@CompileStatic
class RedisService {

    @Value('${redis.host:localhost}')
    String redisHost

    @Value('${redis.port:6379}')
    int redisPort

    JedisPool jedisPool

    @PostConstruct
    void init() {
        jedisPool = new JedisPool(redisHost, redisPort)
    }

    def withRedis(Closure c) {
        Jedis redis = jedisPool.resource
        try {
            return c(redis)
        } catch (JedisConnectionException jce) {
            throw jce
        } catch (Exception e) {
            throw e
        } finally {
            if (redis) {
                redis.close()
            }
        }
    }

    String get(String key) {
        withRedis { Jedis jedis ->
            jedis.get(key)
        }
    }

    String set(String key, String value) {
        withRedis { Jedis jedis ->
            jedis.set(key, value)
        }
    }

    def methodMissing(String name, args) {
        withRedis { Jedis jedis ->
            jedis.invokeMethod(name, args)
        }
    }
}

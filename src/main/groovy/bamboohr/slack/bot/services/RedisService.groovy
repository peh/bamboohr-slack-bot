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

    private JedisPool jedisPool

    @PostConstruct
    void init() {
        jedisPool = new JedisPool(redisHost, redisPort)
    }

    /**
     * takes a Jedis resource from the pool and assigns it to the given Closure to execute successive commands
     * @param callable
     * @return
     */
    def withRedis(@DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = Jedis) Closure callable) {
        Jedis redis = jedisPool.resource
        try {
            callable.setDelegate(redis)
            callable.setResolveStrategy(Closure.DELEGATE_FIRST)
            return callable.call(redis)
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
        String result = null
        withRedis { Jedis jedis ->
            result = get(key)
        }
        result
    }

    String set(String key, String value) {
        String result = null
        withRedis { Jedis jedis ->
            result = set(key, value)
        }
        result
    }

    Object methodMissing(String name, args) {
        return jedisPool.resource.invokeMethod(name, args)
    }
}

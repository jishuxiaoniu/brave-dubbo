package time;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.List;

@Slf4j
public class JedisPoolTime implements Time {

    private Pool pool;

    public JedisPoolTime(Pool pool) {
        this.pool = pool;
    }

    @Override
    public long getMisecond() {
        com.alibaba.dubbo.common.utils.Assert.notNull(pool, "RedisPool can not be null !");
        try(Jedis jedis = (Jedis) pool.getResource();) {
            List<String> source = jedis.time();
            Assert.notEmpty(source, "Received invalid result from server. Expected 2 items in collection.");
            Assert.isTrue(source.size() == 2,
                    "Received invalid nr of arguments from redis server. Expected 2 received " + source.size());

            return NumberUtils.parseNumber(source.get(0), Long.class) * 1000L * 1000L
                    + NumberUtils.parseNumber(source.get(1), Long.class);
        } catch (Exception e) {
            log.error("can not get time from redis !", e);
        }
        return System.currentTimeMillis() * 1000;
    }
}

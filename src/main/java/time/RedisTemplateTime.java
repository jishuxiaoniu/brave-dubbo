package time;

import com.alibaba.dubbo.common.utils.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class RedisTemplateTime implements Time {

    private RedisTemplate redisTemplate;

    public RedisTemplateTime(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public long getMisecond() {
        Assert.notNull(redisTemplate, "RedisTemplate can not be null !");
        return redisTemplate.time();
    }
}

package configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import time.Time;
import time.TimeUtils;

@Configuration
@Slf4j
public abstract class TimeConfiguration {

    @Bean
    public abstract Time time();

    @Bean
    public TimeUtils timeUtils(){
        return new TimeUtils(time());
    }

}

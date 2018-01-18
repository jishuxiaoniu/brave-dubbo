**brave-dubbo**

项目说明：用于集成 brave 到dubbo项目中

使用说明：

    1、项目地址：http://gitlab.yqxiu.cn/duanhuazhen/brave-dubbo.git
       项目依赖：
            <dependency>
                <groupId>com.yqx</groupId>
                <artifactId>brave-dubbo</artifactId>
                <version>1.0.0</version>
            </dependency>
    
    2、在dubbo项目中引入编译后的依赖
    
    3、添加配置文件
    
        #应用名称
        spring.application.name.name=consumer
        # zipkin 服务地址
        trace.brave.zipkin=http://192.168.0.3:9411/api/v2/spans
        # zipkin 数据采集率
        trace.brave.rate=0.1
        
    4、项目依赖于redis，获取微秒级时间，需要实现TimeConfiguration
    如：
        @Configuration
        public class ZipkinTimeConfigura extends TimeConfiguration {
        
            @Resource(name = "activeRedisTemplateNormal")
            private RedisTemplate redisTemplate;
        
            @Bean
            public Time time() {
                return new RedisTemplateTime(redisTemplate);
            }
        }
    
    
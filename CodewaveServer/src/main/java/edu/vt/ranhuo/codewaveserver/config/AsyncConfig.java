package edu.vt.ranhuo.codewaveserver.config;

import edu.vt.ranhuo.asynccore.config.TaskConfig;
import edu.vt.ranhuo.asyncmaster.context.IMaster;
import edu.vt.ranhuo.asyncmaster.context.Master;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AsyncConfig {
    @Bean
    TaskConfig Config(RedissonClient redissonClient) {
        return TaskConfig.builder()
                .prefix("codewave")
                .redissonClient(redissonClient)
                .expirationCount(5)
                .heartbeatInterval(1000 * 30)
                .build();
    }

    // 构建Master
    @Bean
    IMaster<String> Master(TaskConfig config) {
        return new Master(config);
    }

}

package edu.vt.ranhuo.codewaveworker.utils;

import edu.vt.ranhuo.asynccore.config.TaskConfig;
import edu.vt.ranhuo.asyncmaster.context.IMaster;
import edu.vt.ranhuo.asyncmaster.context.Master;
import edu.vt.ranhuo.asyncslave.context.ISlave;
import edu.vt.ranhuo.asyncslave.context.Slave;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.database;

public class AsyncUtil {

    public static ISlave createSlave(){
        TaskConfig taskConfig =  TaskConfig(redissonClient());
        ISlave<String> slave = new Slave(taskConfig);
        return slave;
    }

    private static TaskConfig TaskConfig(RedissonClient redissonClient) {
        return TaskConfig.builder()
                .prefix("codewave")
                .redissonClient(redissonClient)
                .expirationCount(3)
                .heartbeatInterval(10)
                .build();
    }

    private static RedissonClient redissonClient() {
        Config config = new Config();
        config .setCodec(new JsonJacksonCodec())
                .useSingleServer()
                .setDatabase(database.ordinal())
                .setAddress("redis://" + "127.0.0.1" + ":" + "6379");
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}

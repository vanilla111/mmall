package ShardedPoolTest;

import com.shopping.common.RedisShardedPool;
import org.junit.Test;
import redis.clients.jedis.ShardedJedis;

public class TestOne {
    @Test
    public void test(){
        ShardedJedis jedis = RedisShardedPool.getResource();
        for (int i = 0; i < 10; i++) {
            jedis.set("key-" + i, "value-" + i);
        }
        RedisShardedPool.returnResource(jedis);
    }
}

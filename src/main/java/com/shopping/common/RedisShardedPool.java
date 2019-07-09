package com.shopping.common;

import com.shopping.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {
    //连接池
    private static ShardedJedisPool jedisPool;

    //最大连接数、最大空闲连接数、最小空闲连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2 "));

    // 借/还 实例的时候是否检查有效
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "false"));

    private static String redisIP = PropertiesUtil.getProperty("redis_1.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis_1.port"));
    private static String redisPassword = PropertiesUtil.getProperty("redis_1.password");

    private static String redis2IP = PropertiesUtil.getProperty("redis_2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis_2.port"));
    private static String redis2Password = PropertiesUtil.getProperty("redis_2.password");

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //链接耗尽时是否阻塞
        config.setBlockWhenExhausted(true);

        JedisShardInfo info1 = new JedisShardInfo(redisIP, redisPort, 1000 * 2);
        info1.setPassword(redisPassword);
        JedisShardInfo info2 = new JedisShardInfo(redis2IP, redis2Port, 1000 * 2);
        info2.setPassword(redis2Password);

        List<JedisShardInfo> list = new ArrayList<JedisShardInfo>(2);
        list.add(info1);
        list.add(info2);

        jedisPool = new ShardedJedisPool(config, list, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getResource() {
        return jedisPool.getResource();
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public static void returnResource(ShardedJedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = RedisShardedPool.getResource();
        for (int i = 0; i < 50; i++) {
            jedis.set("key-" + i, "value-" + i);
        }
        RedisShardedPool.returnResource(jedis);

    }
}

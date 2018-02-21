package com.shopping.common;

import com.shopping.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    //连接池
    private static JedisPool jedisPool;

    //最大连接数、最大空闲连接数、最小空闲连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2 "));

    // 借/还 实例的时候是否检查有效
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "false"));

    private static String redisIP = PropertiesUtil.getProperty("redis.sentinel.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.sentinel.port"));
    private static String redisPassword = PropertiesUtil.getProperty("redis.sentinel.password");

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        //链接耗尽时是否阻塞
        config.setBlockWhenExhausted(true);

        jedisPool = new JedisPool(config, redisIP, redisPort, 1000 * 2, redisPassword);
    }

    static {
        initPool();
    }

    public static Jedis getResource() {
        return jedisPool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getResource();
        jedis.set("hello","world");
        System.out.println(jedis.get("hello"));
        RedisPool.returnResource(jedis);
        jedisPool.destroy();
    }
}

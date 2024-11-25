package edu.nbd.repositories;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

import java.util.ResourceBundle;

public abstract class AbstractRedisRepository implements AutoCloseable {

    private static JedisPooled pool;

    public void initDbConnection() {
        ResourceBundle rb = ResourceBundle.getBundle("redis");
        String host = rb.getString("redis.host");
        int port = Integer.parseInt(rb.getString("redis.port"));

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder().build();
        pool = new JedisPooled(new HostAndPort(host, port), clientConfig);
    }

    public JedisPooled getPool() {
        if (pool == null) {
            initDbConnection();
        }
        return pool;
    }

    public void clearCache() {
        getPool().flushAll();
    }

    @Override
    public void close() {
        if (pool != null) {
            pool.close();
        }
    }
}

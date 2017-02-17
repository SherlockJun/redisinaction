package redisinaction.c03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import redisinaction.common.BaseTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tanjianhui on 2017/2/16.
 */
public class C03DemoTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(C03DemoTest.class);

    @Test
    public void testRedisString(){
        String stringKey = "stringKey";

        jedis.append(stringKey, "Hello, World!");
        logger.info(jedis.get(stringKey));

        logger.info(jedis.getrange(stringKey, 2, 4));

        jedis.setrange(stringKey, 5, "abc");
        logger.info(jedis.get(stringKey));

        logger.info(jedis.getbit(stringKey, 3).toString());
    }

    @Test
    public void testRedisList(){
        jedis.lpush("list:01", "elem:0101");
        jedis.lpush("list:02", "elem:0201", "elem:0202");
        logger.info("{}", jedis.blpop(10, "list:02", "list:01"));

    }

    @Test
    public void testRedisSet(){
        String setKey01 = "setKey01";
        String setKey02 = "setKey02";
        String setKey03 = "setKey03";
        jedis.sadd(setKey01, "a", "b", "c", "d");
        jedis.sadd(setKey02, "c", "d", "e", "f");

        logger.info("{}", jedis.sdiff(setKey01,setKey02));
        logger.info("{}", jedis.sinter(setKey01,setKey02));
        logger.info("{}", jedis.sunion(setKey01,setKey02));

        logger.info("{}", jedis.sinterstore(setKey03, setKey01,setKey02));
        logger.info("{}", jedis.smembers(setKey03));
    }

    @Test
    public void testRedisHash(){
        Map<String, String> elem = new HashMap<>();

        elem.put("hashKey01", "hashKey01Value");
        elem.put("hashKey02", "hashKey02Value");
        elem.put("hashKey03", "1");
        jedis.hmset("hashKeyName01", elem);
        logger.info("{}", jedis.hmget("hashKeyName01", "hashKey01", "hashKey02"));

        logger.info("{}", jedis.hlen("hashKeyName01"));

        logger.info("{} {}", jedis.hexists("hashKeyName01", "hashKey00"), jedis.hexists("hashKeyName01", "hashKey01"));

        logger.info("{}", jedis.hkeys("hashKeyName01"));

        logger.info("{}", jedis.hvals("hashKeyName01"));

        logger.info("{}", jedis.hgetAll("hashKeyName01"));

        logger.info("{}", jedis.hincrByFloat("hashKeyName01", "hashKey03", .5));

        logger.info("{}", jedis.hincrBy("hashKeyName01", "hashKey04", 5));
    }

    @Test void testRedisZset(){
        String zsetKey = "zsetKey";
        Map<String, Double> elem = new HashMap<>();

        elem.put("member01", Double.valueOf(System.nanoTime()));
        elem.put("member02", Double.valueOf(System.nanoTime()));
        elem.put("member03", Double.valueOf(System.nanoTime()));
        elem.put("member04", 1D);

        jedis.zadd(zsetKey, elem);
        logger.info("{}", jedis.zrange(zsetKey, 0, -1));

        jedis.zrem(zsetKey, "member03");
        logger.info("{}", jedis.zcard(zsetKey));

        logger.info("{}", jedis.zincrby(zsetKey,2, "member04"));
        logger.info("{}", jedis.zscore(zsetKey, "member04"));

        logger.info("{}", jedis.zcount(zsetKey, -1, 5));
        logger.info("{}", jedis.zcount(zsetKey, -1, 2));

        logger.info("{}", jedis.zrank(zsetKey,"member01" ));
        logger.info("{}", jedis.zrank(zsetKey,"member03" ));
    }
}

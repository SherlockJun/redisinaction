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
    public void testRedisHash(){
        Map<String, String> elem = new HashMap<>();

        elem.put("hashKey01", "hashKey01Value");
        elem.put("hashKey02", "hashKey02Value");
        jedis.hmset("hashKeyName01", elem);
        logger.info("{}", jedis.hmget("hashKeyName01", "hashKey01", "hashKey02"));
    }
}

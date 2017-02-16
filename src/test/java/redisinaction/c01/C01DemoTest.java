package redisinaction.c01;

import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import redisinaction.common.BaseTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanjianhui on 2017/2/14.
 */
public class C01DemoTest extends BaseTest{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(C01DemoTest.class);

    private C01Demo c01Demo = new C01Demo();

    @Test
    public void testRedisString(){
        jedis.set("test", "Hello, World!");
        logger.info("[key:test,value:{}]",jedis.get("test"));
        jedis.del("test");
        logger.info("[key:test,value:{}]",jedis.get("test"));
    }

    @Test
    public void testRedisList(){
        String listKey = "listKey";
        jedis.lpush(listKey, "myLeftPush");
        jedis.rpush(listKey, "myRightPush");
        jedis.lpush(listKey, "myLeftPush");
        logger.info("{}", jedis.lrange(listKey, 0, -1));
        logger.info(jedis.lpop(listKey));
        logger.info(jedis.lpop(listKey));
        logger.info(jedis.lpop(listKey));
    }

    @Test
    public void testRedisSet(){
        String setKey = "setKey";
        jedis.sadd(setKey, "sadd");
        jedis.sadd(setKey, "sadd2");
        jedis.sadd(setKey, "sadd3");
        jedis.sadd(setKey, "sadd");
        logger.info("{}", jedis.smembers(setKey));
        logger.info("{}", jedis.sismember(setKey, "sadd4"));
        logger.info("{}", jedis.sismember(setKey, "sadd"));
        logger.info("{}", jedis.srem(setKey, "sadd4"));
        logger.info("{}", jedis.srem(setKey, "sadd3"));
        logger.info("{}", jedis.smembers(setKey));

        // clean
        jedis.srem(setKey, "sadd2");
        jedis.srem(setKey, "sadd1");
    }

    @Test
    public void testRedisHash() {
        String hashKey = "hashKey";

        jedis.hset(hashKey, "subKey1", "value1");
        jedis.hset(hashKey, "subKey2", "value2");
        jedis.hset(hashKey, "subKey1", "value1");
        logger.info("{}", jedis.hgetAll(hashKey));
        jedis.hdel(hashKey, "subKey2");
        jedis.hdel(hashKey, "subKey2");
        logger.info("{}", jedis.hget(hashKey,"subKey1"));
        logger.info("{}", jedis.hgetAll(hashKey));

        // clean
        jedis.hdel(hashKey, "subKey1");
    }

    @Test
    public void testRedisZset(){
        String zsetKey = "zsetKey";

        jedis.zadd(zsetKey, 5, "zsetKey1");
        jedis.zadd(zsetKey, 3, "zsetkey2");
        jedis.zadd(zsetKey, 3, "zsetkey2");
        logger.info("{}", jedis.zrangeWithScores(zsetKey, 0, -1));
        logger.info("{}", jedis.zrange(zsetKey, 0, -1));
        logger.info("{}", jedis.zrangeByScoreWithScores(zsetKey, 0, 4));
        jedis.zrem(zsetKey, "zsetKey1");
        logger.info("{}", jedis.zrangeWithScores(zsetKey, 0, -1));
        logger.info("{}", jedis.zrange(zsetKey, 0, -1));

        // clean
        jedis.zrem(zsetKey, "zsetKey2");
    }

    @Test
    public void testPost(){
        c01Demo.post(jedis, "009", "Redis In Action 2ed", "http://www.redisinaction2ed.com");
    }

    @Test
    public void testGet(){
        List<Map<String, String>> articleList = c01Demo.get(jedis, 1, null);
        System.out.println(!articleList.isEmpty());
    }

    @Test
    public void testVote(){
        c01Demo.vote(jedis, "010", "article:2");
    }

    @Test
    public void testModifyGroup(){
        c01Demo.modifyGroups(jedis, "1", Arrays.asList(new String[]{"group3", "group2"}),
                Arrays.asList(new String[]{"group1"}));
    }

    @Test
    public void testGetByGroup(){
        c01Demo.getByGroup(jedis, "group3", 1, null);
    }

    @Test
    public void test(){
        Set<String> keys = jedis.keys("*");
        for(String key : keys){
            System.out.println(key);
        }
    }

    @Test
    public void testDeleteAllKeys(){
        Set<String> keys = jedis.keys("*");
        if(null != keys && !keys.isEmpty()){
            jedis.del(keys.toArray(new String[]{}));
        }
    }
}

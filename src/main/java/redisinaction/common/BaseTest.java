package redisinaction.common;

import org.testng.annotations.BeforeClass;
import redis.clients.jedis.Jedis;

/**
 * Created by tanjianhui on 2017/2/16.
 */
public class BaseTest {
    protected Jedis jedis;

    @BeforeClass
    public void setup(){
        jedis = new Jedis("dev.mysvr.com", 6379);
    }
}

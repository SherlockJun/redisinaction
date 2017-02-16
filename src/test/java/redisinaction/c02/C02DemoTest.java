package redisinaction.c02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import redisinaction.common.BaseTest;

import java.util.Random;
import java.util.UUID;

/**
 * Created by tanjianhui on 2017/2/16.
 */
public class C02DemoTest extends BaseTest{
    private static final Logger logger = LoggerFactory.getLogger(C02DemoTest.class);

    private C02Demo c02Demo = new C02Demo();

    @Test
    public void testCheckToken(){
        logger.info(c02Demo.checkToken(jedis, "1"));
    }

    @Test
    public void testUpdateToken(){
        c02Demo.updateToken(jedis, "1", "1", "1");
        c02Demo.updateToken(jedis, "1", "1", "2");
        c02Demo.updateToken(jedis, "1", "1", "3");
        c02Demo.updateToken(jedis, "1", "1", "4");
        c02Demo.updateToken(jedis, "1", "1", "5");
        c02Demo.updateToken(jedis, "1", "1", "6");

        int count = new Random().nextInt(100);
        for(int i = 0; i < count; i++){
            c02Demo.updateToken(jedis,
                    UUID.randomUUID().toString().replace("-",""),
                    "user" + new Random().nextInt(1000),
                    "item" + new Random().nextInt(1000));
        }
    }

    @Test
    public void testCleanSession(){
        c02Demo.cleanSessions(jedis);
    }
}

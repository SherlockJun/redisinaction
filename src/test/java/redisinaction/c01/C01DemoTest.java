package redisinaction.c01;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by tanjianhui on 2017/2/14.
 */
public class C01DemoTest {

    private Jedis jedis;
    private C01Demo c01Demo;

    @BeforeClass
    public void setup(){
        jedis = new Jedis("10.10.28.222", 6379);
        c01Demo = new C01Demo();
    }

    @Test
    public void testPost(){
        c01Demo.post(jedis, "007", "Redis In Action", "http://www.redisinaction.com");
    }

    @Test
    public void testGet(){
        List<Map<String, String>> articleList = c01Demo.get(jedis, 1, null);
        System.out.println(!articleList.isEmpty());
    }

    @Test
    public void testVote(){
        c01Demo.vote(jedis, "008", "article:1");
    }

    @Test
    public void testModifyGroup(){
        c01Demo.modifyGroups(jedis, "article:1", Arrays.asList(new String[]{"group3", "group4"}),
                Arrays.asList(new String[]{"group1"}));
    }
}

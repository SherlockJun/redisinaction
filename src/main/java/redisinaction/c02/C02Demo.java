package redisinaction.c02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redisinaction.common.MD5Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by tanjianhui on 2017/2/16.
 */
public class C02Demo {
    private static final Logger logger = LoggerFactory.getLogger(C02Demo.class);
    private static final int LIMIT = 100;

    public String checkToken(Jedis jedis, String token){
        return jedis.hget("login:", token);
    }

    public void updateToken(Jedis jedis, String token, String user, String item){
        long timestamp = System.currentTimeMillis();
        jedis.hset("login:", token, user);
        jedis.zadd("recent:", timestamp, token);

        if(null != item && !item.trim().isEmpty()){
            jedis.zadd("viewed:" + token, timestamp, item);
            jedis.zremrangeByRank("viewed:" + token, 0, -6); // 移除旧的记录，只保留用户最近浏览过的5个商品
        }
    }

    public void cleanSessions(Jedis jedis){
        while(true){
            Long size = jedis.zcard("recent:");
            logger.info("Size is {}.", size);
            if(size <= LIMIT){
                try{
                    Thread.sleep(10000L); // 每10秒扫描一次
                    continue;
                }catch (InterruptedException e){
                    logger.warn(e.getMessage(), e);
                }
            }

            // 每次最多清理10个
            long endIndex = size - LIMIT;
            endIndex = (endIndex < 10 ? endIndex : 10);
            Set<String> tokens = jedis.zrange("recent:", 0, endIndex - 1);

            List<String> sessionKeys = new ArrayList();
            for(String token : tokens){
                sessionKeys.add("viewed:"+token);
            }

            jedis.del(sessionKeys.toArray(new String[]{}));
            jedis.hdel("login:", tokens.toArray(new String[]{}));
            jedis.zrem("recent:", tokens.toArray(new String[]{}));

            logger.info("{} sessions are clean.", sessionKeys.size());
        }
    }

    public void addCart(Jedis jedis, String session, String item, int count){
        if(count <= 0){
            jedis.hdel("cart:" + session, item);
        }else{
            jedis.hset("cart:" + session, item, Integer.toString(count));
        }
    }

    public void cleanFullSessions(Jedis jedis){
        while(true){
            Long size = jedis.zcard("recent:");
            logger.info("Size is {}.", size);
            if(size <= LIMIT){
                try{
                    Thread.sleep(10000L); // 每10秒扫描一次
                    continue;
                }catch (InterruptedException e){
                    logger.warn(e.getMessage(), e);
                }
            }

            // 每次最多清理10个
            long endIndex = size - LIMIT;
            endIndex = (endIndex < 10 ? endIndex : 10);
            Set<String> tokens = jedis.zrange("recent:", 0, endIndex - 1);

            List<String> sessionKeys = new ArrayList();
            for(String token : tokens){
                sessionKeys.add("viewed:"+token);
                sessionKeys.add("cart:"+token);
            }

            jedis.del(sessionKeys.toArray(new String[]{}));
            jedis.hdel("login:", tokens.toArray(new String[]{}));
            jedis.zrem("recent:", tokens.toArray(new String[]{}));

            logger.info("{} sessions are clean.", sessionKeys.size());
        }
    }

    public String cacheRequest(Jedis jedis, String request){
        // 判断请求是否可以缓存。如果不可缓存，则直接调用生成动态页面程序。伪代码:
        // if(!canCache){
        //      return execute(request);
        // }

        String pageKey = "cache:" + MD5Util.encode(request);
        String content = jedis.get(pageKey);

        if (null == content || content.trim().isEmpty()){
            content = "xxx"; // 此处调用execute(request)，并将返回值赋予content
            jedis.setex(pageKey, 300, content);
        }

        return content;
    }
}

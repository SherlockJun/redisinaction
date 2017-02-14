package redisinaction.c01;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ZParams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tanjianhui on 2017/2/14.
 */
public class C01Demo {
    public static final int ONE_WEEK_IN_MILLISECONDS = 7 * 86400 * 1000;
    public static final int VOTE_SCORE = 432;
    public static final int ARTICLES_PER_PAGE = 25;

    /**
     * 发布文章
     * @param jedis
     * @param user
     * @param title
     * @param link
     */
    public String post(Jedis jedis, String user, String title, String link){
        String articleId = jedis.incr("article:").toString();

        String voted = "voted:"+articleId;
        jedis.sadd(voted, user);
        jedis.expire(voted, ONE_WEEK_IN_MILLISECONDS);

        String article = "article:" + articleId;
        jedis.hset(article, "title", title);
        jedis.hset(article, "link", link);
        jedis.hset(article, "poster", user);
        jedis.hset(article, "time", Long.toString(System.currentTimeMillis()));
        jedis.hincrBy(article, "voted", 1);

        jedis.zadd("score:", System.currentTimeMillis() + VOTE_SCORE, article);
        jedis.zadd("time:", System.currentTimeMillis(), article);

        return articleId;
    }

    /**
     * 获取文章
     * @param jedis
     * @param page
     * @param order
     * @return
     */
    public List<Map<String,String>> get(Jedis jedis, int page, String order){
        if(order == null || order.trim().isEmpty()){
            order = "score:";
        }
        int start = (page - 1) * ARTICLES_PER_PAGE;
        int end = start + ARTICLES_PER_PAGE;

        Set<String> ids = jedis.zrevrange(order, start,  end);
        List<Map<String,String>> articleList = new ArrayList<>();
        for (String id : ids) {
            Map<String, String> articleMap = jedis.hgetAll(id);
            articleMap.put("id", id);
            articleList.add(articleMap);
        }

        return articleList;
    }

    /**
     * 对文章投票
     * @param jedis
     * @param user
     * @param article
     */
    public void vote(Jedis jedis, String user, String article){
        long cutoff = System.currentTimeMillis() - ONE_WEEK_IN_MILLISECONDS;
        if(jedis.zscore("time:", article) < cutoff)
            return ;
        String articleId = article.split(":")[1];
        if(jedis.sadd("voted:"+articleId, user) > 0){
            jedis.zincrby("score:", VOTE_SCORE, article);
            jedis.hincrBy(article, "votes", 1);
        }
    }

    /**
     * 修改文章分组
     * @param jedis
     * @param articleId
     * @param toAddGroupList
     * @param toRemoveGroupList
     */
    public void modifyGroups(Jedis jedis, String articleId, List<String> toAddGroupList,
                             List<String> toRemoveGroupList){
        String article = "article:" + articleId;
        for(String group : toAddGroupList){
            jedis.sadd("group:" + group, article);
        }
        for (String group : toRemoveGroupList){
            jedis.srem("group:" + group, article);
        }
    }

    /**
     *
     * @param jedis
     * @param group
     * @param page
     * @param order
     * @return
     */
    public List<Map<String,String>> getByGroup(Jedis jedis, String group, int page, String order){
        if(order == null || order.trim().isEmpty()){
            order = "score:";
        }

        String key = order + group;
        if(!jedis.exists(key)){
            jedis.zinterstore(key, new ZParams().aggregate(ZParams.Aggregate.MAX), "group:" + group, order );
            jedis.expire(key, 60);
        }
        return this.get(jedis, page, key);
    }
}

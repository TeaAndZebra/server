package dataPersistence;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import forwardService.SharedTranMap;
import entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisService implements Runnable{
    private static Logger logger = LogManager.getLogger(RedisService.class.getName());
    private final Jedis jedis;
    private Lock lock = new ReentrantLock();//非公平锁
    public RedisService(Jedis jedis) {
        this.jedis=jedis;
    }

    @Override
    public void run() {
        try {
            updateInRedis(SharedTranMap.getUserList());
        }catch (Exception e){
            logger.debug("[{}] when update in redis  ",e.toString());
        }
    }

    /* *
       * @Title: updateInRedis
       * @Description: 将当前的用户信息存入redis中
       * @param userString:
       * @return boolean
       * @Author: Wang Yueming
       * @Date: 2020/5/11
       */
    private void updateInRedis(ArrayList<User> userArrayList){
       logger.debug("in update redis");
        String userRedisKey=null;
        if(userArrayList==null){
            return ;
        }
        try {
            logger.debug("in user Arraylist");
            for(User user:userArrayList){
                userRedisKey = "u:info:" +user.getPdpSocket();//pdp地址及pdp端口
                Map<String,String> map = new HashMap<>();
                map.put("id","\""+user.getIDString()+"\"");//设备号
                map.put("ad","\""+user.getIpAdd()+"\"");//地址  为啥加\
                map.put("speed", String.valueOf(user.getSpeedOfDatagram()));//速度
                map.put("lOT", "\""+user.getLogOffTime()+"\"");//登出时间
                map.put("lIT", "\""+user.getLogInTime()+"\"");//登入时间
                lock.lock();//保证每15秒更新和remove操作的并发安全
                       if (jedis.exists(userRedisKey)) {
                       jedis.hincrByFloat(userRedisKey, "flow", user.getBitsOfDatagram());
                   } else {
                       jedis.hset(userRedisKey, "flow", String.valueOf(user.getBitsOfDatagram()));
                   }
                   jedis.hmset(userRedisKey, map);
               lock.unlock();
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }



}

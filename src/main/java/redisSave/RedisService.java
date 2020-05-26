package redisSave;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import forwardService.SharedTranMap;
import forwardService.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RedisService implements Runnable{
    private static Logger logger = LogManager.getLogger(RedisService.class.getName());
    private ArrayList<User> userArrayList = new ArrayList<>();
    private Jedis jedis;
    private SharedTranMap userMap=new SharedTranMap();
    public RedisService(Jedis jedis) {
        this.jedis=jedis;
    }

    @Override
    public void run() {
        updateInRedis(userMap.getUserList());
    }

    /* *
       * @Title: serialize
       * @Description: 将用户信息序列化
       * @param userArrayList:
       * @return boolean
       * @Author: Wang Yueming
       * @Date: 2020/5/11
       */
    public String serialize(ArrayList<User> userArrayList){
        return JSONObject.toJSONString(userArrayList);
    }
    /* *
       * @Title: updateInRedis
       * @Description: 将当前的用户信息存入redis中
       * @param userString:
       * @return boolean
       * @Author: Wang Yueming
       * @Date: 2020/5/11
       */
    public boolean updateInRedis(ArrayList<User> userArrayList){
        this.userArrayList=userArrayList;
        String userRedisKey=null;
        if(userArrayList==null){
            return true;
        }
        try {
            for(User user:userArrayList){
                userRedisKey = "user:info:" +user.getPdpSocket();//pdp地址及pdp端口
                Map<String,String> map = new HashMap<>();
                if(jedis.exists(userRedisKey)){
                    jedis.hincrByFloat(userRedisKey, "flow",user.getBitsOfDatagram());
                }else{
                    jedis.hset(userRedisKey, "flow", String.valueOf(user.getBitsOfDatagram()));
                }
                map.put("address",user.getIpAdd()+":"+user.getIpPort());
                map.put("speed", String.valueOf(user.getSpeedOfDatagram()));
                map.put("logOffTime", user.getLogOffTime());
                map.put("logInTime", user.getLogInTime());
                jedis.hmset(userRedisKey,map);

            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }
}

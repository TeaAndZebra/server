package forwardService;

import dataPersistence.RedisInterface;
import dataPersistence.RedisService;
import entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Remover implements Runnable {
    private volatile User user;
    private static Logger logger = LogManager.getLogger(Remover.class.getName());
    public Remover(User user) {
        this.user = user;
    }
    @Override
    public void run() {
             logger.info("[{}] is removed", user.toString());
             removeSetRedis();
             removeFromCache();
        }

    public  void removeFromCache(){
        if (SharedTranMap.pdpSocketUserMap.containsValue(user)) {
//                System.out.println( user.getPdpSocket().getPdpAdd()+" :  "+user.getPdpSocket().getPdpPort()+" is removed");
            /**
             * 2020/6/1 避免对pdpPortMap并发操作
             * */
            SharedTranMap.pdpPortMap.remove(user.getPdpSocket().getPdpAdd(), user.getPdpSocket().getPdpPort());
            SharedTranMap.pdpSocketUserMap.remove(user.getPdpSocket(), user);
            SharedTranMap.regImplWithObject.remove(user);

        }
    }

    public void removeSetRedis(){//登出时要设置redis的登出时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        user.getTimer().cancel(false);
        user.setLogOffTime(dateFormat.format(new Date()));
        user.getCalSpeedFuture().cancel(false);
        Jedis jedis=null;
        try {
            jedis = new Jedis("127.0.0.1",6379);
            RedisService service = new RedisService(jedis);

            RedisInterface redisService = service::run;
            redisService.updateInRedis();//调用函数式接口的方法
            logger.info("log out and update redis : [{}]",redisService);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }finally {
            if(jedis!=null) {
                jedis.close();
            }
        }

    }

    }

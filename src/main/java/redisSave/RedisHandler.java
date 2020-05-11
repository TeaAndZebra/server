package redisSave;

import redis.clients.jedis.Jedis;
import server79.Pdp;
import server79.PdpSocket;
import server79.SharedTranMap;

import java.math.BigDecimal;
import java.util.Map;
/**
 * redisSave.RedisHandler
 * 每15s在redis数据库中存入当前数据流量
 * redisSave.MysqlHandler
 * 每天0点在mysql数据库中插入item（一天的流量），并清空redis数据库流量
 * **/
public class RedisHandler implements Runnable{
    private Jedis jedis;
    public RedisHandler(Jedis jedis){
        this.jedis = jedis;
    }

    @Override
    public void run() {
       //System.out.println(System.currentTimeMillis()+"  run ");

        /**
         * 多次获取并设置是否能改为mget mset，节省时间
         * */
        for (Map.Entry<PdpSocket, Pdp> entry : SharedTranMap.pdpSocketPdpMap.entrySet()){
            Pdp pdp = entry.getValue();
          //  System.out.println(pdp+" "+pdp.getPdpSocket().getPdpAdd()+" : "+pdp.getPdpSocket().getPdpPort()+"  ,time is "+System.currentTimeMillis()+" num is  "+pdp.getBitsOfDatagram());
            if(pdp.getBitsOfDatagram()!=0){
                double flow = pdp.getBitsOfDatagram()/100.0;//MB
                BigDecimal bg = new BigDecimal(flow);
                double flowNow = bg.setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue();
                Double flowPre = jedis.zscore("UserFlow", pdp.getPdpSocket().getPdpAdd()+":"+pdp.getPdpSocket().getPdpPort());
                if(flowPre!=null){
                    jedis.zincrby("UserFlow",flowNow,pdp.getPdpSocket().getPdpAdd()+":"+pdp.getPdpSocket().getPdpPort() );
                }else {
                    jedis.zadd("UserFlow",flowNow,pdp.getPdpSocket().getPdpAdd()+":"+pdp.getPdpSocket().getPdpPort() );
                }
                pdp.setBitsOfDatagram(0);
                System.out.println(" redis flowNow is  " +  flowNow+flowPre);
            }
        }
    }

}

package redisSave;

import redis.clients.jedis.Jedis;
import server79.Pdp;
import server79.PdpSocket;
import server79.SharedTranMap;

import java.util.Map;

public class RedisHandler implements Runnable {
    private Jedis jedis;
    public RedisHandler(Jedis jedis){
        this.jedis = jedis;
    }

    @Override
    public void run() {
        for (Map.Entry<PdpSocket, Pdp> entry : SharedTranMap.pdpSocketPdpMap.entrySet()){
            Pdp pdp = entry.getValue();
            if(pdp.getBitsOfDatagram()!=0){
                long flowNow = pdp.getBitsOfDatagram();
                byte[] flowPreByte = jedis.get((pdp.getPdpSocket().getPdpAdd()+":"+pdp.getPdpSocket().getPdpPort()).getBytes());
                if(flowPreByte!=null) {
                    long flowPre = Long.parseLong(new String(flowPreByte));
//                    System.out.println("redis flowPre is  " + flowPre);
                    flowNow += flowPre;
                    pdp.setBitsOfDatagram(0);
//                    System.out.println("redis flowNow is  " +  flowNow);

                }
                jedis.set((pdp.getPdpSocket().getPdpAdd()+":"+pdp.getPdpSocket().getPdpPort()).getBytes(),String.valueOf(flowNow).getBytes());
                pdp.setBitsOfDatagram(0);
            }
        }
    }

}

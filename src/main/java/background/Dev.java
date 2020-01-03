package background;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import server79.Pdp;
import server79.PdpSocket;
import server79.SharedTranMap;

import java.util.Map;

class Dev {
    private Jedis jedis = new Jedis("localhost");
   String getInfo() {
       JSONArray array = new JSONArray();
       for (Map.Entry<PdpSocket,Pdp> entry :SharedTranMap.pdpSocketPdpMap.entrySet()){
           Pdp pdp = entry.getValue();
           Double flowPre = jedis.zscore("UserFlow", pdp.getPdpSocket().getPdpAdd()+":"+pdp.getPdpSocket().getPdpPort());
           pdp.setCurrentFlow(flowPre);
           array.add(pdp);
       }

       JSONObject result = new JSONObject();
       result.put("info", array);
       return result.toJSONString();

  }
}

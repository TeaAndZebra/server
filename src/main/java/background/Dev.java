package background;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import server79.Pdp;
import server79.PdpSocket;
import server79.SharedTranMap;

import java.util.Map;

class Dev {
   String getInfo() {
       JSONArray array = new JSONArray();
       for (Map.Entry<PdpSocket,Pdp> entry :SharedTranMap.objectWithSocket.entrySet()){
           Pdp pdp = entry.getValue();
           if(SharedTranMap.pdpPortMap.containsValue(pdp.getPdpSocket().getPdpAdd(), pdp.getPdpSocket().getPdpPort())){
               pdp.setActive(true);
           }else {
               pdp.setActive(false);
           }
           array.add(pdp);
       }

       JSONObject result = new JSONObject();
       result.put("info", array);
       return result.toJSONString();

  }
}

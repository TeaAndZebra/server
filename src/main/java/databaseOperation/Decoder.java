package databaseOperation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Decoder {
    private String mode;
    private JSONObject cmd;
    public Decoder(JSONObject cmd){
        this.cmd = cmd;
    }
    public String getMode() {
        cmd.getString("mode");
        return mode;
    }
}

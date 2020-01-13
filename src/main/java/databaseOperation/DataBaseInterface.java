package databaseOperation;

import com.alibaba.fastjson.JSONObject;

public interface DataBaseInterface {
    JSONObject acquireUserDetails();
    JSONObject addUserDev();
    JSONObject delUserDev();
    JSONObject modDevLink();
    JSONObject configDev();
    JSONObject logIn();
}

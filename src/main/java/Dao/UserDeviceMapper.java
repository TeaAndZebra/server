package Dao;

import databaseOperation.Device;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface UserDeviceMapper {
    void addUserDev(@Param("userId") String userId, @Param("devId") String devId);
    ArrayList<String> selectUserDev(String userId);
    void delUserDev(@Param("userId")String userId, @Param("devId")String devId);
}

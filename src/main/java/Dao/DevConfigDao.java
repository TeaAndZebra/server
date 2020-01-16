package Dao;

import databaseOperation.Device;
import org.apache.ibatis.annotations.Param;

public interface DevConfigDao {
    Device selectDevConfByID(String devId);
    void insertDevConf(Device device);
    void  updateDevConfig(Device device);
    String selectDevAvail(String devId);
}

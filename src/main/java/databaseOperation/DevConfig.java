package databaseOperation;

import java.util.ArrayList;

public class DevConfig {
    private User user;
    private ArrayList<Device> deviceList;

    public DevConfig(User user, ArrayList<Device> deviceList) {
        this.user = user;
        this.deviceList = deviceList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(ArrayList<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public void addDev(Device dev){
        this.deviceList.add(dev);
    }
}

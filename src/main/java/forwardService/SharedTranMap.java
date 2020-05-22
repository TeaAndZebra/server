package forwardService;

import org.apache.commons.collections.map.MultiValueMap;
import forwardService.reg.RegImpl;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/* *
   * @Title: SharedTranMap
   * @Description: 提供服务器上所需的用户及信息映射关系
   * @param null:
   * @return
   * @Author: Wang Yueming
   * @Date: 2020/5/12
   */
public class SharedTranMap {
    public static MultiValueMap pdpPortMap = new MultiValueMap();/**(Int32位 pdpAdd)和byte pdpPort     线程不安全*/
    public static ConcurrentHashMap<PdpSocket, User> pdpSocketPdpMap = new ConcurrentHashMap<>();
   // public static ConcurrentHashMap<PdpSocket,User> finalPdpSocketPdpMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<User, RegImpl> regImplWithObject = new ConcurrentHashMap<>();

    private ArrayList<User> userList =null;
/* *
   * @Title: getUserList
   * @Description: 获得当前所有的用户
   * @param :
   * @return java.util.ArrayList<forwardService.User>
   * @Author: Wang Yueming
   * @Date: 2020/5/12
   */
    public ArrayList<User> getUserList() {
        if(pdpSocketPdpMap==null||pdpSocketPdpMap.size()==0){
            return null;
        }
        for(Map.Entry<PdpSocket,User> entry:pdpSocketPdpMap.entrySet()){
            userList.add(entry.getValue());
        }
        return userList;
    }
}

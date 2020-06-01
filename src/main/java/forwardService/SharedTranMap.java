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

    //是否需要加volatile保证并发访问pdpPortMap安全？  volatile
    //仅仅解决了可见性的问题， 但是它并不能保证互斥性， 也就是说多个线程并发修改某个变
    //量时， 依旧会产生多线程问题。 因此， 不能靠volatile来完全替代传统的锁

    public   static MultiValueMap pdpPortMap = new MultiValueMap();/**(Int32位 pdpAdd)和byte pdpPort     线程不安全*/
    public static ConcurrentHashMap<PdpSocket,User> pdpSocketPdpMap = new ConcurrentHashMap<>();
    // public static ConcurrentHashMap<PdpSocket,Pdp> finalPdpSocketPdpMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<User,RegImpl> regImplWithObject = new ConcurrentHashMap<>();

    /**
     * 2020/6/1 避免并发访问pdpPortMap
     * 只要是对pdpPortMap的操作都需要被锁
     * */
    public static synchronized void modPdpPortMap(String type,int pdpAddInt, byte pdpPort){
        if(type.equals("put")){
            pdpPortMap.put(pdpAddInt,pdpPort);
        }
       if(type.equals("remove")){
           pdpPortMap.remove(pdpAddInt,pdpPort);
       }
    }

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

package server79;

import org.apache.commons.collections.map.MultiValueMap;

import java.util.concurrent.ConcurrentHashMap;

public class SharedTranMap {
    public static MultiValueMap pdpPortMap = new MultiValueMap();/**(Int32位 pdpAdd)和byte pdpPort     线程不安全*/
    public static ConcurrentHashMap<PdpSocket,Pdp> pdpSocketPdpMap = new ConcurrentHashMap<>();
   // public static ConcurrentHashMap<PdpSocket,Pdp> finalPdpSocketPdpMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Pdp,RegImpl> regImplWithObject = new ConcurrentHashMap<>();
}

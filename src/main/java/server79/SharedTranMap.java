package server79;

import org.apache.commons.collections.map.MultiValueMap;

import java.util.concurrent.ConcurrentHashMap;

public class SharedTranMap {
    public static MultiValueMap pdpPortMap = new MultiValueMap();/**(Int32位 pdpAdd)和byte pdpPort*/
    public static ConcurrentHashMap<PdpSocket,Pdp> objectWithSocket = new ConcurrentHashMap<>();

}

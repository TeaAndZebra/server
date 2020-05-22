package databaseOperation;

import Dao.DevConfigDao;
import Dao.LinksRelationshipDao;
import Dao.UserDeviceMapper;
import Dao.UserMapper;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import server.impl.UserServerImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DeviceSetting {
    private JSONObject mode;
    private SqlSessionFactory sqlSessionFactory = (new instrument.SqlSession()).getSqlSessionFactory();
    private static Logger logger = LogManager.getLogger(DeviceSetting.class.getName());

    public DeviceSetting(JSONObject mode) {
        this.mode = mode;
    }

    public String acquireUserDetails(JSONObject cmd) throws IOException {
        JSONObject jsonObject = new JSONObject();
        String userId;
        if(cmd.getString("user_id")!=null){
            userId = cmd.getString("user_id").trim();
        }else {
            logger.debug("Json 格式不正确");
            jsonObject.put("msg", "Json 格式不正确");
            return jsonObject.toString();
        }
        ArrayList<String> devList;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            if(userMapper.findByUserId(userId)==null) {
                jsonObject.put("state", "fail");
                jsonObject.put("msg", "当前用户不存在");
                return jsonObject.toString();
            }
            UserDeviceMapper userDeviceMapper = sqlSession.getMapper(UserDeviceMapper.class);
            devList = userDeviceMapper.selectUserDev(userId);
            if(devList.size()==0){
                jsonObject.put("state","fail");
                jsonObject.put("msg","当前用户还未添加设备");
                return jsonObject.toString();
            }else {
                Map<String,Device> userDevMap = new ConcurrentHashMap<>();
                for(String devId:devList){
                    DevConfigDao devConfigDao = sqlSession.getMapper(DevConfigDao.class);
                    Device device =  devConfigDao.selectDevConfByID(devId);
                    System.out.println(device);
                    //      SrcDevConfig srcDevConfig = srcDevConfigDao.selectSrcDevConfByID(devId);
                    if(device!=null){
                        Map<String, Map<String, List<String>>> links = new ConcurrentHashMap<>();
                        HashMap<String,List<String>> videoLinksMap = new HashMap<>();
                        LinksRelationshipDao linksRelationshipDao = sqlSession.getMapper(LinksRelationshipDao.class);
                        for(int i=0;i<16;i++){
                            if(!(linksRelationshipDao.selectLinksBySource(devId,"video",i)).isEmpty()){
                                videoLinksMap.put("LinkVideo"+i,linksRelationshipDao.selectLinksBySource(devId,"video",i));
                            }
                        }
                        HashMap<String,List<String>>  audioLinksMap = new HashMap<>();
                        for(int i=0;i<16;i++){
                            if(!(linksRelationshipDao.selectLinksBySource(devId,"audio",i)).isEmpty()){
                                audioLinksMap.put("LinkAudio"+i,linksRelationshipDao.selectLinksBySource(devId,"audio",i));
                            }
                        }
                        HashMap<String,List<String>>  boxLinksMap = new HashMap<>();
                        for(int i=0;i<16;i++){
                            if(!(linksRelationshipDao.selectLinksBySource(devId,"box",i)).isEmpty()){
                                boxLinksMap.put("LinkBox"+i,linksRelationshipDao.selectLinksBySource(devId,"box",i));
                            }
                        }
                        HashMap<String,List<String>>  cmdLinksMap = new HashMap<>();
                        for(int i=0;i<16;i++){
                            if(!(linksRelationshipDao.selectLinksBySource(devId,"cmd",i)).isEmpty()){
                                cmdLinksMap.put("LinkCmd"+i,linksRelationshipDao.selectLinksBySource(devId,"cmd",i));
                            }
                        }
                        links.put("video",videoLinksMap);
                        links.put("audio",audioLinksMap);
                        links.put("box",boxLinksMap);
                        links.put("cmd",cmdLinksMap);
                        device.setLinks(links);
                        userDevMap.put(devId, device);
                    }
                }
                jsonObject.put("state","success");
                jsonObject.put("dev_list",userDevMap);
                return jsonObject.toString();
            }
        }finally {
            sqlSession.close();
        }
    }

    public String addUserDev(JSONObject cmd) {
        JSONObject jsonObject = new JSONObject();
        String userId;
        String devId;
        if(cmd.getString("user_id")!=null&&cmd.getString("dev_id")!=null){
            userId = cmd.getString("user_id").trim();
            devId = cmd.getString("dev_id").trim();
        }else {
            logger.debug("Json 格式不正确");
            jsonObject.put("msg", "Json 格式不正确");
            return jsonObject.toString();
        }

        //检查上传设备参数
        if(devId==null || devId.length()==0){
            jsonObject.put("state","fail");
            jsonObject.put("msg","请输入有效的设备ID");
            return jsonObject.toString();
        }

        if(userId==null || userId.length()==0){
            jsonObject.put("state","fail");
            jsonObject.put("msg","请输入有效的用户ID");
            return jsonObject.toString();
        }
        //检查是否在备案的设备库中
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            DevConfigDao devConfigDao = sqlSession.getMapper(DevConfigDao.class);
            if(devConfigDao.selectDevAvail(devId)==null){
                jsonObject.put("state","fail");
                jsonObject.put("msg",devId+"不是合法的设备");
                return jsonObject.toString();
            }
            /************************************************************************/
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user =  userMapper.findByUserId(userId);
            if(user==null){
                jsonObject.put("state","fail");
                jsonObject.put("msg","当前用户不存在");
                return jsonObject.toString();
            }
            /***********************************************************************/

            UserDeviceMapper userDeviceMapper = sqlSession.getMapper(UserDeviceMapper.class);
            ArrayList<String> userDevList = userDeviceMapper.selectUserDev(userId);

            if(userDevList.contains(devId)){
                jsonObject.put("state","fail");
                jsonObject.put("msg","当前设备已在配置中");
                return jsonObject.toString();
            }else{
                userDeviceMapper.addUserDev(userId, devId);
                sqlSession.commit();
                jsonObject.put("state","success");
                jsonObject.put("msg","添加设备成功");
            }
            return jsonObject.toString();
        }finally {
            sqlSession.close();
        }

    }


    public String delUserDev(JSONObject cmd) {
        JSONObject jsonObject = new JSONObject();
        String userId;
        String devId;
        if(cmd.containsKey("user_id")&&cmd.containsKey("dev_id")){
            userId = cmd.getString("user_id").trim();
            devId = cmd.getString("dev_id").trim();
        }else {
            logger.debug("Json 格式不正确");
            jsonObject.put("msg", "Json 格式不正确");
            return jsonObject.toString();
        }

        //检查上传设备参数
        if(devId==null || devId.length()==0){
            jsonObject.put("state","fail");
            jsonObject.put("msg","请输入有效的设备ID");
            return jsonObject.toString();
        }

        if(userId==null || userId.length()==0){
            jsonObject.put("state","fail");
            jsonObject.put("msg","请输入有效的用户ID");
            return jsonObject.toString();
        }
        //检查是否在备案的设备库中
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            if(userMapper.findByUserId(userId)==null) {
                jsonObject.put("state", "fail");
                jsonObject.put("msg", "当前用户不存在");
                return jsonObject.toString();
            }
            DevConfigDao devConfigDao = sqlSession.getMapper(DevConfigDao.class);

            if(devConfigDao.selectDevAvail(devId)==null){
                jsonObject.put("state","fail");
                jsonObject.put("msg",devId+"不是合法的设备");
                return jsonObject.toString();
            }
            /***********************************************************/
            UserDeviceMapper userDeviceMapper = sqlSession.getMapper(UserDeviceMapper.class);
            ArrayList<String> devList = userDeviceMapper.selectUserDev(userId);
            if(devList.contains(devId)){
                userDeviceMapper.delUserDev(userId,devId);
                LinksRelationshipDao linksRelationshipDao = sqlSession.getMapper(LinksRelationshipDao.class);
                linksRelationshipDao.delAllLinksBySource(devId);
                //提交事务
                sqlSession.commit();
                jsonObject.put("msg","删除成功 ");
                return jsonObject.toString();
            }else{
                jsonObject.put("devId",cmd.get("devId"));
                jsonObject.put("state","fail");
                jsonObject.put("msg","当前用户还未添加设备"+devId);
                jsonObject.put("links",null);
                return jsonObject.toString();
            }

        }finally {
            sqlSession.close();
        }

    }


    public String modDevLink(JSONObject cmd) {
        JSONObject jsonObject = new JSONObject();
        String operate,userId,srcDevId,destDevId,type;
        int channel;
        if(cmd.containsKey("operate")&&cmd.containsKey("user_id")&&cmd.containsKey("src_dev_id")&&cmd.containsKey("dest_dev_id")&&cmd.containsKey("type")
                &&cmd.containsKey("channel")){
            operate = cmd.getString("operate").trim();
            userId = cmd.getString("user_id").trim();
            srcDevId = cmd.getString("src_dev_id").trim();
            destDevId = cmd.getString("dest_dev_id").trim();
            type = cmd.getString("type").trim();
            channel =cmd.getInteger("channel");
        }else {
            logger.debug("Json 格式不正确");
            jsonObject.put("msg", "Json 格式不正确");
            return jsonObject.toString();
        }

        if(srcDevId==null || srcDevId.length()==0){
            jsonObject.put("state","fail");
            jsonObject.put("msg","源设备参数错误");
            return jsonObject.toString();
        }
        if( destDevId==null ||destDevId.length()==0){
            jsonObject.put("state","fail");
            jsonObject.put("msg","目的设备参数错误");
            return jsonObject.toString();
        }
        if(destDevId.equals(srcDevId)){
            jsonObject.put("state","fail");
            jsonObject.put("msg","源设备与目的设备不能相同");
            return jsonObject.toString();
        }
        if(channel>15 || channel <0){
            jsonObject.put("state","fail");
            jsonObject.put("msg","通道数超出范围");
            return jsonObject.toString();
        }
        if(!type.equals("video")&&!type.equals("audio")&&!type.equals("box")&&!type.equals("command"))
        {
            jsonObject.put("state","fail");
            jsonObject.put("msg","链接类型参数错误");
            return jsonObject.toString();
        }
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {

            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            if(userMapper.findByUserId(userId)==null){
                jsonObject.put("state","fail");
                jsonObject.put("msg","当前用户不存在");
                return jsonObject.toString();
            }
            jsonObject.put("userId",userId);
            UserDeviceMapper userDeviceMapper = sqlSession.getMapper(UserDeviceMapper.class);
            if(!userDeviceMapper.selectUserDev(userId).contains(srcDevId)){
                jsonObject.put("state","fail");
                jsonObject.put("msg","用户未添加源设备"+srcDevId);
                return jsonObject.toString();
            }
            if(!userDeviceMapper.selectUserDev(userId).contains(destDevId)){
                jsonObject.put("state","fail");
                jsonObject.put("msg","用户未添加目的设备"+destDevId);
                return jsonObject.toString();
            }
            LinksRelationshipDao linksRelationshipDao = sqlSession.getMapper(LinksRelationshipDao.class);
            LinksRelationship linksRelationship = new LinksRelationship(srcDevId,destDevId,type,channel);
            switch (operate){
                case "add":{
                    if(linksRelationshipDao.selectLinksBySource(srcDevId,type,channel).contains(destDevId)){
                        jsonObject.put("state","fail");
                        jsonObject.put("msg","该链接已经存在");
                    }else {
                        linksRelationshipDao.insertLinks(linksRelationship);
                        sqlSession.commit();
                        jsonObject.put("msg","添加链接成功 ");
                    }
                    break;
                }
                case "delete":{
                    if(!linksRelationshipDao.selectLinksBySource(srcDevId,type,channel).contains(destDevId)){
                        jsonObject.put("state","fail");
                        jsonObject.put("msg","该链接不存在");
                    }else {
                        linksRelationshipDao.delLinksBySource(linksRelationship);
                        sqlSession.commit();
                        jsonObject.put("state","success");
                        jsonObject.put("msg", "删除成功");
                    }
                    break;
                }
                default:{
                    jsonObject.put("state","fail");
                    jsonObject.put("msg", "mode格式错误");
                    break;
                }
            }
            //检查用户参数
            return jsonObject.toString();

        }finally {
            sqlSession.close();
        }
    }


    public String configDev(JSONObject cmd) {
        JSONObject jsonObject = new JSONObject();
        String userId,devId,frame,resolution,codec,codeRate;
        if(cmd.containsKey("user_id")&&cmd.containsKey("dev_id")&&cmd.containsKey("frame")&&cmd.containsKey("resolution")&&cmd.containsKey("codec")
                &&cmd.containsKey("code_rate")){
            userId = cmd.getString("user_id").trim();
            devId = cmd.getString("dev_id").trim();
            frame = cmd.getString("frame").trim();
            resolution = cmd.getString("resolution").trim();
            codec = cmd.getString("codec").trim();
            codeRate = cmd.getString("code_rate").trim();
        }else {
            logger.debug("Json 格式不正确");
            jsonObject.put("msg", "Json 格式不正确");
            return jsonObject.toString();
        }

        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            if(userId==null || userId.length()==0){
                jsonObject.put("state","fail");
                jsonObject.put("msg","用户id错误");
                return jsonObject.toString();
            }
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            if(userMapper.findByUserId(userId)==null) {
                jsonObject.put("state", "fail");
                jsonObject.put("msg", "当前用户不存在");
                return jsonObject.toString();
            }
            UserDeviceMapper userDeviceMapper = sqlSession.getMapper(UserDeviceMapper.class);
            if(!userDeviceMapper.selectUserDev(userId).contains(devId)){
                jsonObject.put("state","fail");
                jsonObject.put("msg","用户未添加该设备"+devId);
                return jsonObject.toString();
            }
            Device device = new Device(devId,codeRate,codec,frame,resolution);
            DevConfigDao devConfigDao = sqlSession.getMapper(DevConfigDao.class);
            if(devConfigDao.selectDevConfByID(devId)==null){
                devConfigDao.insertDevConf(device);
                sqlSession.commit();
            }else {
                devConfigDao.updateDevConfig(device);
                sqlSession.commit();
            }
            jsonObject.put("devId",device.getDevId());
            jsonObject.put("Codec",device.getEncodec());
            jsonObject.put("FrameRate",device.getFrame());
            jsonObject.put("Resolution",device.getResolution());
            jsonObject.put("CodeRate",device.getCodeRate());

            jsonObject.put("state","success");
            jsonObject.put("msg","配置成功");
            return jsonObject.toString();


        }finally {
            sqlSession.close();
        }
    }


    public String logIn(JSONObject cmd) {
        JSONObject jsonObject = new JSONObject();
        String name;
        String password;
        if(cmd.getString("user_id")!=null&&cmd.getString("password")!=null){
            name = cmd.getString("user_id").trim();
            password = cmd.getString("password").trim();
        }else {
            jsonObject.put("msg", "Json 格式不正确");
            System.out.println(jsonObject.toString());
            return jsonObject.toString();
        }
        if(name.length()<3 || password.length()<3){
            jsonObject.put("state","fail");
            jsonObject.put("msg","用户名或密码错误");
            System.out.println(jsonObject.toString());
            return jsonObject.toString();
        }
        Map<String,Object> map = new UserServerImpl().login(name, password);
        if (map.containsKey("ticket")) {
            jsonObject.put("state","success");
            jsonObject.put("ticket",map.get("ticket").toString());
            jsonObject.put("msg","登录成功");
        } else {
            jsonObject.put("msg", map.get("msg"));
        }
        System.out.println(jsonObject.toString());
        return jsonObject.toString();
    }
}

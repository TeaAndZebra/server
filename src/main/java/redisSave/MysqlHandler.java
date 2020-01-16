package redisSave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import server79.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 * redisSave.RedisHandler
 * 每15s在redis数据库中存入当前数据流量
 * redisSave.MysqlHandler
 * 每天0点在mysql数据库中插入item（一天的流量），并清空redis数据库流量
 * **/
public class MysqlHandler {
    private  Connection connection = null;
    private  Statement statement = null;
    private  String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private  String DB_URL = "jdbc:mysql://39.97.171.14:3306/webrtclive?"
            +"user=root&password=123abc&useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT";//&autoReconnect=true
    private  final long PERIOD_MINUTE = 60 * 1000;
    static Logger logger = LogManager.getLogger(MysqlHandler.class.getName());


    public void insertData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        if(date.before(new Date())){
            date = addDay(date, 1);
        }
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                logger.info("insert into mysql");
                /**connect to mysql database*/
                Jedis jedis = new Jedis("localhost");
                try {
                    Class.forName(JDBC_DRIVER);
                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
                try {
                    connection = DriverManager.getConnection(DB_URL);
                } catch (SQLException e) {
//                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }

                //  System.out.println("实例化statement对象...");
                try {
                    statement = connection.createStatement();
                } catch (SQLException e) {
//                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }

                for(Map.Entry< PdpSocket, Pdp> entry : SharedTranMap.pdpSocketPdpMap.entrySet()) {
                    Pdp pdp = entry.getValue();
                    Double flowD = jedis.zscore("UserFlow", pdp.toString());
                    logger.debug("[{}] previous user flow is [{}]",pdp.toString(),flowD);
                    Long flow = flowD != null ? flowD.longValue() : null;
                    if (flow != null && flow != 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");//
                        Date date = new Date();// 获取当前时间
                        String time = sdf.format(date);
                        logger.debug("time is [{}]",time);
//                    System.out.println("time is : " + time+" now"); // 输出已经格式化的现在时间（24小时制）
                        try {
                            String sql = "INSERT INTO user_daily_flow(user,flow,time) " +
                                    "VALUES ('" + pdp.toString() + "','" + flow + "','" + time + "')";
                            statement.execute(sql);
                            /**redis 清0*/
                            try {
                                jedis.zadd("UserFlow", 0, pdp.toString());
                            }catch (JedisConnectionException e){
                                logger.error(e.getMessage(), e);
                            }

                        } catch (SQLException se) {
//                            se.printStackTrace();
                            logger.error(se.getMessage(), se);
                        } catch (Exception e) {
//                           e.printStackTrace();
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
                try {
                    connection.close();
                } catch (SQLException e) {
//                    e.printStackTrace();
                    logger.error(e.getMessage(), e);
                }
            }
        };
        timer.schedule(timerTask, date,PERIOD_MINUTE);

    }
    private Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.MINUTE, num);
    //    startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}

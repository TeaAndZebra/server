package redisSave;

import redis.clients.jedis.Jedis;
import server79.DataBase;
import server79.Pdp;
import server79.PdpSocket;
import server79.SharedTranMap;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

public class MysqlHandler {
    private  Connection connection = null;
    private  Statement statement = null;
    private  String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private  String DB_URL = "jdbc:mysql://39.97.171.14:3306/webrtclive?"
            +"user=root&password=123abc&useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT";//&autoReconnect=true
    private  final long PERIOD_DAY =  24 * 60 * 60 * 1000;


    public void insertData() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
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
                /**connect to mysql database*/
                Jedis jedis = new Jedis("localhost");
                try {
                    Class.forName(JDBC_DRIVER);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    connection = DriverManager.getConnection(DB_URL);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //  System.out.println("实例化statement对象...");
                try {
                    statement = connection.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                for(Map.Entry< PdpSocket, Pdp> entry : SharedTranMap.pdpSocketPdpMap.entrySet()) {
                    Pdp pdp = entry.getValue();
                    Double flowD = jedis.zscore("UserFlow", pdp.getPdpSocket().getPdpAdd() + ":" + pdp.getPdpSocket().getPdpPort());
                    Long flow = flowD != null ? flowD.longValue() : null;
                    if (flow != null && flow != 0) {
                        String userStr = (pdp.getPdpSocket().getPdpAdd() + ":" + pdp.getPdpSocket().getPdpPort());
                        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");//
                        Date date = new Date();// 获取当前时间
                        String time = sdf.format(date);
//                    System.out.println("time is : " + time+" now"); // 输出已经格式化的现在时间（24小时制）
                        try {
                            String sql = "INSERT INTO user_daily_flow(user,flow,time) " +
                                    "VALUES ('" + userStr + "','" + flow + "','" + time + "')";
                            statement.execute(sql);
                            /**redis 清0*/
                            jedis.zadd("UserFlow", 0, pdp.getPdpSocket().getPdpAdd() + ":" + pdp.getPdpSocket().getPdpPort());
                        } catch (SQLException se) {
                            se.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(timerTask, date,PERIOD_DAY);

    }
    private Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}

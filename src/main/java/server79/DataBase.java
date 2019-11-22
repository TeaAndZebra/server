package server79;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class DataBase {
   private Connection connection = null;
   private Statement statement = null;
   private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
   private String DB_URL = "jdbc:mysql://39.97.171.14:3306/webrtclive?"
           +"user=root&password=123abc&useUnicode=true&characterEncoding=UTF-8";//&autoReconnect=true
    private Jedis jedis;
    private static Logger logger = LogManager.getLogger(DataBase.class.getName());
  public DataBase(){
       this.initial();
   }
   public Connection getConnection() {
       return connection;
   }

   private void initial(){
       try {
           Class.forName(JDBC_DRIVER);
       } catch (ClassNotFoundException e) {
//           e.printStackTrace();
           logger.error(e.getMessage(),e);
       }

       //  System.out.println("连接数据库...");
       //  System.out.println("实例化statement对象...");
       try {
           connection = DriverManager.getConnection(DB_URL);
           statement = connection.createStatement();
       } catch (SQLException e) {
//           e.printStackTrace();
           logger.error(e.getMessage(),e);
       }
   }
    protected int getUserAdd(String user_id){

        int userAdd=0;
        try{
            String sql;

            sql = "SELECT user_add From user_ip where user_id='"+user_id+"'";//user_id:deviceID user_add:pdpAdd
            ResultSet rs = statement.executeQuery(sql);
            //  System.out.println(rs.toString());
            while ((rs.next())){
                // String user_id = rs.getString("user_id");
                userAdd = rs.getInt("user_add");
                System.out.println("usrAdd:"+userAdd);
            }

            rs.close();
           // System.out.println(connection.isValid(2));
          //  connection.close();

        }catch (SQLException se){
//            se.printStackTrace();
            logger.error(se.getMessage(),se);
        } catch (Exception e){
//            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        return userAdd;

    }
    protected boolean containPdpAdd(int pdpAddInt) {
        boolean contain = false;
        try {

            String sql;

            sql = "SELECT user_add From user_ip ";
            ResultSet rs = statement.executeQuery(sql);
            //  System.out.println(rs.toString());
            while ((rs.next())) {
                // String user_id = rs.getString("user_id");
                int add = rs.getInt("user_add");
                if(add==pdpAddInt){
                 contain = true;
                }
            }

            rs.close();

        } catch (SQLException se) {
//            se.printStackTrace();
            logger.error(se.getMessage(),se);
        } catch (Exception e) {
//            e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        return contain;
    }

}

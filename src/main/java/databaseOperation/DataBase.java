package databaseOperation;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.sql.*;

public class DataBase  implements DataBaseInterface{
    private JSONObject cmd;
    private Connection connection = null;
    private Statement statement = null;
    private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String DB_URL = "jdbc:mysql://39.97.171.14:3306/webrtclive?"
            +"user=root&password=123abc&useUnicode=true&characterEncoding=UTF-8";//&autoReconnect=true
    private static Logger logger = LogManager.getLogger(server79.DataBase.class.getName());
    public DataBase(JSONObject cmd){
        this.cmd = cmd;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage(),e);
        }
        try {
            connection = DriverManager.getConnection(DB_URL);
            statement = connection.createStatement();
        } catch (SQLException e) {
            logger.error(e.getMessage(),e);
        }
    }

    @Override
    public JSONObject acquireUserDetails() {
        String userId = cmd.getString("user_id");
        JSONObject jsonObject = new JSONObject();
        try{
            String sql;
            sql = "SELECT * From user where user_id='"+userId+"'";//user_id:deviceID user_add:pdpAdd
            ResultSet rs = statement.executeQuery(sql);
            //  System.out.println(rs.toString());
            while ((rs.next())){
                // String user_id = rs.getString("user_id");
                rs.getInt("user_add");
            }

            rs.close();
            // System.out.println(connection.isValid(2));
            //  connection.close();

        }catch (SQLException se){
            // se.printStackTrace();
            logger.error(se.getMessage(),se);
        } catch (Exception e){
            // e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public JSONObject addUserDev() {

        return null;
    }

    @Override
    public JSONObject delUserDev() {
        return null;
    }

    @Override
    public JSONObject modDevLink() {
        return null;
    }

    @Override
    public JSONObject configDev() {
        return null;
    }

    @Override
    public JSONObject logIn() {
        return null;
    }
}

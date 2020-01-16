import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import server79.DataBase;

import java.sql.*;

public class Database {
    private Connection connection = null;
    private Statement statement = null;
    private String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String DB_URL = "jdbc:mysql://39.97.171.14:3306/webrtclive?"
            +"user=root&password=123abc&useUnicode=true&characterEncoding=UTF-8";//&autoReconnect=true
    private static Logger logger = LogManager.getLogger(DataBase.class.getName());

    public static void main(String[] args) {
        new Database().initial();
    }
    private void initial(){
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

        int userAdd=0;
        try{
            String sql;

            sql = "SELECT * From user where user_id='"+"321"+"'";//user_id:deviceID user_add:pdpAdd
            ResultSet rs = statement.executeQuery(sql);
            while ((rs.next())){


                // String user_id = rs.getString("user_id");
                userAdd = rs.getInt("user_add");
                System.out.println("usrAdd:"+userAdd);
            }

            rs.close();

        }catch (SQLException se){
            // se.printStackTrace();
            logger.error(se.getMessage(),se);
        } catch (Exception e){
            // e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
    }
}

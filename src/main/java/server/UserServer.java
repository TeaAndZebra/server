package server;
import databaseOperation.User;

import java.awt.print.Pageable;
import java.util.Map;
import java.util.Set;


public interface UserServer {

//    User findByUserId(int userId);
///****2019.8.5  21:09**/
//
//    String getPassword(String name);
    Map<String, Object> login(String userId, String password);
    String addLoginTicket(int userId);
    //分页
    //Page<User> findAll(Pageable pageable);
}


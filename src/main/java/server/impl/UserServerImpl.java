package server.impl;
import Dao.LoginTicketDao;
import Dao.UserMapper;
import databaseOperation.LoginTicket;
import databaseOperation.User;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.crypto.hash.SimpleHash;
import server.UserServer;
import server79.NonRegHandler;

import java.util.*;
//implements UserServer之前
public class UserServerImpl  implements UserServer{
    private SqlSessionFactory sqlSessionFactory = (new instrument.SqlSession()).getSqlSessionFactory();
    private static Logger logger = LogManager.getLogger(NonRegHandler.class.getName());

    public UserServerImpl()  {
    }

    public Map<String, Object> login(String userId, String password)  {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(userId)) {
            map.put("msg", "用户名不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空");
            return map;
        }

        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            User user = userMapper.findByUserId(userId);
            if (user == null) {
                map.put("msg", "用户不存在");
                return map;
            }

            String passwordDB = user.getPassword();
            String salt = user.getSalt();
            String passwordEncode = new SimpleHash("MD5",password,salt,2).toString();
            int verify = user.getVerify();
            if(verify==0){
                map.put("msg", "您的账号还没进行邮件验证，请点击验证邮件里的链接完成注册！");
                return map;
            }
            if(!passwordEncode.equals(passwordDB)){
                map.put("msg", "密码不正确");
                return map;
            }
            String ticket = addLoginTicket(user.getId());
            map.put("ticket", ticket);
            return map;
        }finally {
            sqlSession.close();
        }

    }
    public String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            LoginTicketDao loginTicketDao = sqlSession.getMapper(LoginTicketDao.class);
            loginTicketDao.addTicket(ticket);
            logger.info("ticket is [{}]", ticket.toString());
            sqlSession.commit();
            return ticket.getTicket();
        }finally {
            sqlSession.close();
        }

    }

//    @Override
//    public User findByUserId(int userId) {
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        try {
//            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//            return userMapper.findByUserId(String.valueOf(userId));
//        }finally {
//            sqlSession.close();
//        }
//
//    }
//
//
//
//    @Override
//    public String getPassword(String name){
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
//        return userMapper.getPassword(name);
//    }


}

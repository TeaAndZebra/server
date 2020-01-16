package Dao;

import databaseOperation.User;
import org.apache.ibatis.annotations.Mapper;

public interface UserMapper {
    User findByUserId(String userId);
}

package databaseOperation;

import java.util.ArrayList;

public class User {
//    auto increment (primary key)
    private int id;
    private String userId;
    private String password;
    private String userEmail;
    private String salt;
    private int verify;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getVerify() {
        return verify;
    }

    public void setVerify(int verify) {
        this.verify = verify;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", salt='" + salt + '\'' +
                ", verify=" + verify +
                '}';
    }
}

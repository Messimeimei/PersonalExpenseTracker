package com.example.personalexpensetracker.data.dao;

import com.example.personalexpensetracker.data.model.User;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Query("DELETE  FROM user")
    void deleteAllUsers();

    @Query("SELECT * FROM user WHERE phone = :phone LIMIT 1")
    User getUserByPhone(String phone);


    @Query("SELECT * FROM user WHERE nickname = :username AND password = :password LIMIT 1")
    User login(String username, String password);


}



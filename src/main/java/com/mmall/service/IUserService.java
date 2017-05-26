package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by wang on 2017/5/12.
 */
public interface IUserService  {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> getForgetQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwodNew, String token);

    ServerResponse<String> resetPassword(User user, String passowrdOld, String passwordNew);

    ServerResponse<User> update_information(User user);

    ServerResponse<User> get_information(int userid);

    //backend
    ServerResponse checkAmin(User user);
}

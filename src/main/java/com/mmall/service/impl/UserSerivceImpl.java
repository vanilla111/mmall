package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by wang on 2017/5/12.
 */
@Service("iUserService")
public class UserSerivceImpl implements IUserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultUserId = userMapper.checkUsername(username);
        if (resultUserId == 0)
            return ServerResponse.createByErrorMessage("user not found");

        //todo 密码登陆 MD5
        String md5_password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5_password);
        if (user == null)
            return ServerResponse.createByErrorMessage("password wrong");

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("login success", user);
    }

    public ServerResponse<String> register(User user) {
        ServerResponse serverResponse = this.checkValid(user.getUsername(), "username");
        if (!serverResponse.isSuccess()) {
            return serverResponse;
        }
        ServerResponse serverResponse1 = this.checkValid(user.getEmail(), "email");
        if (!serverResponse1.isSuccess()) {
            return serverResponse1;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5 加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultUserId = userMapper.insert(user);
        if (resultUserId == 0 )
            return ServerResponse.createByErrorMessage("failed to register, please try again!");

        return ServerResponse.createBySuccessMessage("success");
    }

    public ServerResponse<String> checkValid(String str, String type) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
            //开始校验
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0)
                    return ServerResponse.createByErrorMessage("username already exists");
                else
                    return ServerResponse.createBySuccessMessage("true");
            }

            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0)
                    return ServerResponse.createByErrorMessage("email has used");
                else
                    return ServerResponse.createBySuccessMessage("true");
            }

            return ServerResponse.createByErrorMessage("param wrong");
        } else {
            return ServerResponse.createByErrorMessage("type is blank");
        }
    }

    public ServerResponse<String> getForgetQuestion(String username) {
        ServerResponse serverResponse = this.checkValid(username, "username");
        if (serverResponse.isSuccess())
            return ServerResponse.createByErrorMessage("user not found");

        String  question = userMapper.selectQuestionByUsername(username);

        if (StringUtils.isNotBlank(question))
            return ServerResponse.createBySuccess("success", question);

        return ServerResponse.createByErrorMessage("question is not set");
    }

    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //问题正确
            String forget_token = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forget_token);
            return ServerResponse.createBySuccess("success", forget_token);
        }

        return ServerResponse.createByErrorMessage("answer is wrong");
    }

    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String token) {
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("token is required");
        }
        ServerResponse serverResponse = this.checkValid(username, "username");
        if (serverResponse.isSuccess())
            return ServerResponse.createByErrorMessage("user not found");

        String cache_toekn = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(cache_toekn)) {
            return ServerResponse.createByErrorMessage("token is invalid or expired");
        }

        if (StringUtils.equals(token, cache_toekn)) {
            //验证成功，更新密码
            String md5_pasword = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5_pasword);
            if (rowCount > 0)
                return ServerResponse.createBySuccessMessage("success to reset password");

            return ServerResponse.createByErrorMessage("failed");
        }

        return ServerResponse.createByErrorMessage("token is invalid or expired");
    }

    public ServerResponse<String> resetPassword(User user, String passowrdOld, String passwordNew) {
        //校验旧密码是否正确
        String md5_passwordOld = MD5Util.MD5EncodeUtf8(passowrdOld);
        int resultCount = userMapper.checkPassword(user.getId(), md5_passwordOld);
        if (resultCount > 0 ) {
            user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
            int updateCount = userMapper.updateByPrimaryKeySelective(user);
            if (updateCount > 0)
                return ServerResponse.createBySuccessMessage("success to reset passowrd");
        } else {
            return ServerResponse.createByErrorMessage("原密码错误，修改密码失败");
        }

        return ServerResponse.createByErrorMessage("failed to reset password");
    }

    public ServerResponse<User> update_information(User user) {
        //username不能被更新
        //email需要校验
        int resultCount = userMapper.checkEmailByUserId(user.getId(), user.getEmail());
        if (resultCount > 0)
            return ServerResponse.createByErrorMessage("email has used");

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0)
            return ServerResponse.createBySuccess("succee to update information", user);

        return ServerResponse.createByErrorMessage("failed to update information");
    }

    public ServerResponse<User> get_information(int userid) {
        User user = userMapper.selectByPrimaryKey(userid);
        if (user == null)
            return ServerResponse.createByErrorMessage("user not found");

        user.setPassword("");
        return ServerResponse.createBySuccess("success", user);
    }

    //backend

    public ServerResponse checkAmin(User user) {
        if (user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN)
            return ServerResponse.createBySuccess();

        return ServerResponse.createByError();
    }
}

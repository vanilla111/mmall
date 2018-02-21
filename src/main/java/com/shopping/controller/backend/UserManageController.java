package com.shopping.controller.backend;

import com.shopping.common.Const;
import com.shopping.common.ServerResponse;
import com.shopping.pojo.User;
import com.shopping.service.IUserService;
import com.shopping.util.CookieUtil;
import com.shopping.util.JsonUtil;
import com.shopping.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by wang on 2017/5/13.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);
        if (serverResponse.isSuccess()) {
            User user = serverResponse.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                CookieUtil.writeLoginToken(response, session.getId());
                RedisPoolUtil.setex(session.getId(), JsonUtil.objectToJson(user), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return serverResponse;
            } else {
                return ServerResponse.createByErrorMessage("权限不足");
            }
        }

        return serverResponse;
    }
}

package com.shopping.controller.protal;

import com.shopping.common.Const;
import com.shopping.common.ServerResponse;
import com.shopping.pojo.User;
import com.shopping.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by wang on 2017/5/12.
 */
@Controller
@RequestMapping("/user/springsession")
public class SpringSeesionUserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登陆
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);
        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());

//            CookieUtil.writeLoginToken(response, session.getId());
//            RedisShardedPoolUtil.setex(session.getId(), JsonUtil.objectToJson(serverResponse.getData()),
//                    Const.RedisCacheExtime.REDIS_SESSION_EXTIME);

        }

        return serverResponse;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        session.removeAttribute(Const.CURRENT_USER);
//        String loginToken = CookieUtil.readLoginToken(request);
//        CookieUtil.delLoginCookie(request, response);
//        RedisShardedPoolUtil.del(loginToken);

        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session, HttpServletRequest request) {
//        String loginToken = CookieUtil.readLoginToken(request);
//        User user = null;
//        if (StringUtils.isNotEmpty(loginToken))
//            user = JsonUtil.stringToObj(RedisShardedPoolUtil.get(loginToken), User.class);

        User user = (User) session.getAttribute(Const.CURRENT_USER);

        if (user == null)
            return ServerResponse.createByErrorMessage("用户尚未登录");

        return ServerResponse.createBySuccess(user);
    }


}

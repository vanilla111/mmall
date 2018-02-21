package com.shopping.controller.backend;

import com.shopping.common.Const;
import com.shopping.common.ResponseCode;
import com.shopping.common.ServerResponse;
import com.shopping.pojo.User;
import com.shopping.service.ICategoryService;
import com.shopping.service.IUserService;
import com.shopping.util.CookieUtil;
import com.shopping.util.JsonUtil;
import com.shopping.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wang on 2017/5/14.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("/add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request,
                                      String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisPoolUtil.get(loginToken), User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "please login");
        }
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("权限不足");
        }

        return iCategoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping("/set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest request, Integer categoryId, String categoryName) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisPoolUtil.get(loginToken), User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "please login");
        }
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("权限不足");
        }

        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping("/get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpServletRequest request,
                                                      @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisPoolUtil.get(loginToken), User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "please login");
        }
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("权限不足");
        }

        //查询子节点的信息，保持平级
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    @RequestMapping("/get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildren(HttpServletRequest request,
                                                     @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisPoolUtil.get(loginToken), User.class);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "please login");
        }
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("权限不足");
        }

        //查询当前id对应节点的信息和所有属于它子节点的信息
        return iCategoryService.getCategoryAndChildren(categoryId);
    }
}

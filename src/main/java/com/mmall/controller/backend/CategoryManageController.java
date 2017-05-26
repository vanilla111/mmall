package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

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
    public ServerResponse addCategory(HttpSession session,
                                      String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerResponse setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerResponse getChildrenParallelCategory(HttpSession session,
                                                      @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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
    public ServerResponse getCategoryAndDeepChildren(HttpSession session,
                                                     @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
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

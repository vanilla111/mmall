package com.shopping.controller.backend;

import com.google.common.collect.Maps;
import com.shopping.common.Const;
import com.shopping.common.ResponseCode;
import com.shopping.common.ServerResponse;
import com.shopping.pojo.Product;
import com.shopping.pojo.User;
import com.shopping.service.IFileService;
import com.shopping.service.IProductService;
import com.shopping.service.IUserService;
import com.shopping.util.CookieUtil;
import com.shopping.util.JsonUtil;
import com.shopping.util.PropertiesUtil;
import com.shopping.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by wang on 2017/5/14.
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse saveProduct(HttpServletRequest request, Product product) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisPoolUtil.get(loginToken), User.class);

        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "please login");

        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("权限不足");
        }

        return iProductService.saveOrUpdateProduct(product);
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setProductStatus(HttpServletRequest request, Integer productId, Integer status) {
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisPoolUtil.get(loginToken), User.class);

        if (user == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "please login");

        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            return ServerResponse.createByErrorMessage("权限不足");
        }

        return iProductService.setProductStatus(productId, status);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpServletRequest request, Integer productId) {
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

        return iProductService.manageProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpServletRequest request,
                                  @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
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

        return iProductService.getList(pageNum, pageSize);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpServletRequest request, String productName, Integer productId,
                                  @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
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

        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpServletRequest request,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file) {
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
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        return ServerResponse.createBySuccess(fileMap);
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(@RequestParam(value = "upload_file", required = false) MultipartFile file,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Map resMap = Maps.newHashMap();
        String loginToken = CookieUtil.readLoginToken(request);
        User user = null;
        if (StringUtils.isNotEmpty(loginToken))
            user = JsonUtil.stringToObj(RedisPoolUtil.get(loginToken), User.class);
        if (user == null) {
            resMap.put("success", false);
            resMap.put("msg", "请先登录！");
            return resMap;
        }
        if (user.getRole() != Const.Role.ROLE_ADMIN) {
            resMap.put("success", false);
            resMap.put("msg", "权限不足！");
            return resMap;
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if (StringUtils.isBlank(targetFileName)) {
            resMap.put("success", false);
            resMap.put("msg", "上传失败！");
            return resMap;
        }
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        resMap.put("success",true);
        resMap.put("msg", "上传成功");
        resMap.put("file_path", url);

        response.setHeader("Access-Control-Allow-Headers", "X-File-Name");

        return resMap;
    }
}

package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wang on 2017/5/14.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product == null)
            return ServerResponse.createByErrorMessage("param wrong");

        if (StringUtils.isNotBlank(product.getSubImages())) {
            String[] subImageArray = product.getSubImages().split(",");
            if (subImageArray.length > 0) {
                product.setMainImage(subImageArray[0]);
            }
        }

        int rowCount = 0;
        if (product.getId() != null) {
            rowCount = productMapper.updateByPrimaryKey(product);
            if (rowCount > 0)
                return ServerResponse.createBySuccessMessage("success to update product");
            else
                return ServerResponse.createByErrorMessage("failed to update product");
        } else {
            rowCount = productMapper.insert(product);
            if (rowCount > 0)
                return ServerResponse.createBySuccessMessage("success to save new product");
            else
                return ServerResponse.createByErrorMessage("failed to save new product");
        }
    }

    public ServerResponse setProductStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0)
            return ServerResponse.createBySuccessMessage("success to set the status of the product");

        return ServerResponse.createByErrorMessage("failed to set the status");
    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = (Product) productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("product maybe has been removed ");

        //vo object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());;
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //image host
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "image.wangweisong.com/"));

        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            //默认根节点
            productDetailVo.setParentCategoryId(0);
        } else {
            //否则设置响应的父亲节点
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //createtime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));

        //updatetime
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ServerResponse<PageInfo> getList(int pageNum, int pageSize) {
        //start page
        PageHelper.startPage(pageNum, pageSize);
        //sql
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : productList) {
            ProductListVo productListVo = assemableProductListVo(product);
            productListVoList.add(productListVo);
        }
        //end page
        PageInfo pageRes = new PageInfo(productList);
        pageRes.setList(productListVoList);
        return ServerResponse.createBySuccess(pageRes);
    }

    private ProductListVo assemableProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setStatus(product.getStatus());

        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix",
                "image.wangweisong.com"));

        return productListVo;
    }

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectListByNameAndId(productName, productId);
        List<ProductListVo> productListVoList = new ArrayList<>();
        for (Product product : productList) {
            ProductListVo productListVo = assemableProductListVo(product);
            productListVoList.add(productListVo);
        }
        //end page
        PageInfo pageRes = new PageInfo(productList);
        pageRes.setList(productListVoList);
        return ServerResponse.createBySuccess(pageRes);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = (Product) productMapper.selectByPrimaryKey(productId);
        if (product == null)
            return ServerResponse.createByErrorMessage("product maybe has been removed ");

        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode())
            return ServerResponse.createByErrorMessage("product maybe has been removed ");
        //vo object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                                                                Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null)
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        List<Integer> categoryList = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)) {
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }

            categoryList = iCategoryService.getCategoryAndChildren(categoryId).getData();
        }

        if (StringUtils.isNoneBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }

        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.productListOrderBy.PRICE_DESC_ASC.contains(orderBy)) {
                String[] orderByArr = orderBy.split("_");
                PageHelper.orderBy(orderByArr[0] + " " + orderByArr[1]);
            }
        }

        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectListByNameAndCategoryids(StringUtils.isBlank(keyword) ? null : keyword,
                categoryList.size() == 0 ? null : categoryList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            productListVoList.add(assemableProductListVo(product));
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }
}

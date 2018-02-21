package com.shopping.service;

import com.github.pagehelper.PageInfo;
import com.shopping.common.ServerResponse;
import com.shopping.pojo.Product;
import com.shopping.vo.ProductDetailVo;

/**
 * Created by wang on 2017/5/14.
 */
public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse setProductStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                                                         Integer pageNum, Integer pageSize, String orderBy);
}

package com.shopping.service;

import com.shopping.common.ServerResponse;
import com.shopping.pojo.Category;

import java.util.List;

/**
 * Created by wang on 2017/5/14.
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> getCategoryAndChildren(Integer categoryId);
}

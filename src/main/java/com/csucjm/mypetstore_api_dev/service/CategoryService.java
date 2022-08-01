package com.csucjm.mypetstore_api_dev.service;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.Category;

import java.util.List;

public interface CategoryService {

    //获取单个category信息的详情
    CommonResponse<Category> getCategory(Integer categoryId);

    //获取一个category信息的一级子分类列表, 非递归
    CommonResponse<List<Category>> getChildrenCategories(Integer categoryId);

    //获取一个category信息的所有子分类的ID, 递归查询
    CommonResponse<List<Integer>> getCategoryAndAllChildren(Integer categoryId);

    //新增category
    CommonResponse<Object> addCategory(String categoryName, Integer productId);

    CommonResponse<Object> updateCategory(Integer categoryId, String categoryName);

}

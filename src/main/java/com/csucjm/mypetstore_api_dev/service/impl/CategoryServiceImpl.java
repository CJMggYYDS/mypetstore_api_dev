package com.csucjm.mypetstore_api_dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.entity.Category;
import com.csucjm.mypetstore_api_dev.persistence.CategoryMapper;
import com.csucjm.mypetstore_api_dev.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("categoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;


    @Override
    public CommonResponse<Category> getCategory(Integer categoryId) {
        if(categoryId == null) {
            return CommonResponse.createForError("查询分类信息时，ID不能为空");
        }
        if(CONSTANT.CATEGORY_ROOT.equals(categoryId)) {
            return CommonResponse.createForError("分类的根节点无详细信息");
        }

        Category category = categoryMapper.selectById(categoryId);
        if(category == null) {
            return CommonResponse.createForError("无该分类信息");
        }
        return CommonResponse.createForSuccess(category);
    }

    @Override
    public CommonResponse<List<Category>> getChildrenCategories(Integer categoryId) {
        if(categoryId == null) {
            return CommonResponse.createForError("查询分类信息时，ID不能为空");
        }

        QueryWrapper<Category> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id", categoryId);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);

        if(categoryList.isEmpty()) {
            log.info("非递归查询分类信息的一级子分类时，没有查询到相关子分类信息");
        }

        return CommonResponse.createForSuccess(categoryList);
    }

    private Set<Category> findChildCategory(Integer categoryId, Set<Category> categorySet) {
        Category category = categoryMapper.selectById(categoryId);
        if(category != null) {
            categorySet.add(category);
        }

        QueryWrapper<Category> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("parent_id", categoryId);
        List<Category> categoryList=categoryMapper.selectList(queryWrapper);
        for(Category categoryItem : categoryList) {
            findChildCategory(categoryItem.getId(), categorySet);
        }
        return categorySet;
    }

    @Override
    public CommonResponse<List<Integer>> getCategoryAndAllChildren(Integer categoryId) {
        Set<Category> categorySet = new HashSet<>();
        List<Integer> categoryIdList = new ArrayList<>();

        if(categoryId == null) {
            return CommonResponse.createForSuccess(categoryIdList);
        }

        findChildCategory(categoryId, categorySet);

        for(Category category : categorySet) {
            categoryIdList.add(category.getId());
        }
        return CommonResponse.createForSuccess(categoryIdList);
    }

    @Override
    public CommonResponse<Object> addCategory(String categoryName, Integer productId) {
        return null;
    }

    @Override
    public CommonResponse<Object> updateCategory(Integer categoryId, String categoryName) {
        return null;
    }
}

package com.csucjm.mypetstore_api_dev.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csucjm.mypetstore_api_dev.common.CONSTANT;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.common.ResponseCode;
import com.csucjm.mypetstore_api_dev.entity.Category;
import com.csucjm.mypetstore_api_dev.entity.Product;
import com.csucjm.mypetstore_api_dev.persistence.CategoryMapper;
import com.csucjm.mypetstore_api_dev.persistence.ProductMapper;
import com.csucjm.mypetstore_api_dev.service.CategoryService;
import com.csucjm.mypetstore_api_dev.service.ProductService;
import com.csucjm.mypetstore_api_dev.utils.ImageServerConfig;
import com.csucjm.mypetstore_api_dev.vo.ProductDetailVO;
import com.csucjm.mypetstore_api_dev.vo.ProductListVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("productService")
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    @Resource
    private ImageServerConfig imageServerConfig;


    @Override
    public CommonResponse<ProductDetailVO> getProductDetail(Integer productId) {
        if(productId == null) {
            return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(), ResponseCode.ARGUMENT_ILLEGAL.getMsg());
        }

        Product product = productMapper.selectById(productId);

        if(product == null) {
            return CommonResponse.createForError("商品不存在");
        }
        if(product.getStatus() != CONSTANT.ProductStatus.ON_SALE.getCode()) {
            return CommonResponse.createForError("商品不在售，或已经下架");
        }
        return CommonResponse.createForSuccess(productToProductDetailVO(product));
    }

    @Override
    public CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyword, String orderBy, int pageNum, int pageSize) {
        if(StringUtils.isBlank(keyword) && categoryId == null) {
            return CommonResponse.createForError("分类ID和keyword至少必须提交一项");
        }
        if(categoryId != null) {
            Category category = categoryMapper.selectById(categoryId);
            if(category==null && StringUtils.isBlank(keyword)) {
                log.info("没有查到分类ID为 {} 的商品信息,且keyword未指定", categoryId);
                return CommonResponse.createForSuccess();
            }
        }
        Page<Product> pageResult = new Page<>();
        pageResult.setCurrent(pageNum);
        pageResult.setSize(pageSize);

        QueryWrapper<Product> queryWrapper=new QueryWrapper<>();

        //增加按分类查询
        List<Integer> categoryIdList = categoryService.getCategoryAndAllChildren(categoryId).getData();
        if(categoryIdList.size() != 0) {
            queryWrapper.in("category_id", categoryIdList);
        }

        //增加关键字模糊查询
        if(StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("name", keyword);
        }

        //增加排序规则(依前端的约定之后扩展)
        if(StringUtils.isNotBlank(orderBy)) {
            if(StringUtils.equals(orderBy, CONSTANT.PRODUCT_ORDER_BY_PRICE_ASC)) {
                queryWrapper.orderByAsc("price");
            }
            else if(StringUtils.equals(orderBy, CONSTANT.PRODUCT_ORDER_BY_PRICE_DESC)) {
                queryWrapper.orderByDesc("price");
            }
        }

        pageResult = productMapper.selectPage(pageResult, queryWrapper);

        Page<ProductListVO> productListVOPage = toProductListVOPage(pageResult);

        return CommonResponse.createForSuccess(productListVOPage);
    }


    private ProductDetailVO productToProductDetailVO(Product product) {
        ProductDetailVO productDetailVO = new ProductDetailVO();

        productDetailVO.setId(product.getId());
        productDetailVO.setCategoryId(product.getCategoryId());
        productDetailVO.setName(product.getName());
        productDetailVO.setSubtitle(product.getSubtitle());
        productDetailVO.setPrice(product.getPrice());
        productDetailVO.setDetail(product.getDetail());
        productDetailVO.setStatus(product.getStatus());
        productDetailVO.setStock(product.getStock());
        productDetailVO.setMainImage(product.getMainImage());
        productDetailVO.setSubImages(product.getSubImages());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        productDetailVO.setCreateTime(formatter.format(product.getCreateTime()));
        productDetailVO.setUpdateTime(formatter.format(product.getUpdateTime()));

        Category category = categoryMapper.selectById(product.getCategoryId());
        productDetailVO.setParentCategoryId(category.getParentId());

        productDetailVO.setImageServer(imageServerConfig.getUrl()+","+imageServerConfig.getUsername()+","+imageServerConfig.getPassword());

        return productDetailVO;
    }

    private ProductListVO productToProductListVO(Product product) {
        ProductListVO productListVO = new ProductListVO();

        productListVO.setId(product.getId());
        productListVO.setSubtitle(product.getSubtitle());
        productListVO.setName(product.getName());
        productListVO.setCategoryId(product.getCategoryId());
        productListVO.setPrice(product.getPrice());
        productListVO.setStatus(product.getStatus());
        productListVO.setMainImage(product.getMainImage());

        productListVO.setImageServer(imageServerConfig.getUrl()+","+imageServerConfig.getUsername()+","+imageServerConfig.getPassword());

        return productListVO;
    }

    private Page<ProductListVO> toProductListVOPage(Page<Product> result) {
        List<ProductListVO> productListVOList = new ArrayList<>();
        for(Product item : result.getRecords()) {
            ProductListVO productListVO = productToProductListVO(item);
            productListVOList.add(productListVO);
        }

        Page<ProductListVO> newResult = new Page<>();
        newResult.setCurrent(result.getCurrent());
        newResult.setSize(result.getSize());
        newResult.setTotal(result.getTotal());

        newResult.setRecords(productListVOList);

        return newResult;
    }
}

package com.csucjm.mypetstore_api_dev.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.vo.ProductDetailVO;
import com.csucjm.mypetstore_api_dev.vo.ProductListVO;

public interface ProductService {

    CommonResponse<ProductDetailVO> getProductDetail(Integer productId);

    CommonResponse<Page<ProductListVO>> getProductList(Integer categoryId, String keyword, String orderBy, int pageNum, int pageSize);
}

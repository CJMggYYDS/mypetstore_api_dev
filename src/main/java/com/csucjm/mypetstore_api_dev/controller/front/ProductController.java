package com.csucjm.mypetstore_api_dev.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.service.ProductService;
import com.csucjm.mypetstore_api_dev.vo.ProductDetailVO;
import com.csucjm.mypetstore_api_dev.vo.ProductListVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/product")
@Validated
public class ProductController {

    @Resource
    private ProductService productService;

    @GetMapping("/detail")
    public CommonResponse<ProductDetailVO> getProductDetail(
            @RequestParam @NotNull(message = "商品Id不能为空") Integer productId
    ) {
        return productService.getProductDetail(productId);
    }

    @GetMapping("/list")
    public CommonResponse<Page<ProductListVO>> getProductList(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "") String orderBy,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize)
    {
        return productService.getProductList(categoryId, keyword, orderBy, pageNum, pageSize);
    }
}

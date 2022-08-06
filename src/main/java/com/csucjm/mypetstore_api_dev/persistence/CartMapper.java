package com.csucjm.mypetstore_api_dev.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csucjm.mypetstore_api_dev.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}

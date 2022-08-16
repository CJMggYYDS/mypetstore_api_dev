package com.csucjm.mypetstore_api_dev.persistence;

import com.csucjm.mypetstore_api_dev.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper extends ExpandBaseMapper<OrderItem> {
}

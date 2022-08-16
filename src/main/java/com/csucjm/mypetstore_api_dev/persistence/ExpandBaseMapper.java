package com.csucjm.mypetstore_api_dev.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ExpandBaseMapper<T> extends BaseMapper<T> {

    int insertBatchSomeColumn(List<T> entityList);
}

package com.caih.cloud.iscs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.caih.cloud.iscs.model.entity.StoreInfo;
import com.caih.cloud.iscs.model.qo.ScStoreQo;
import com.caih.cloud.iscs.model.qo.StoreQo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zengheng
 * @date 2019/08/05
 */
@Component
@Mapper
public interface ScStoreMapper extends BaseMapper<StoreInfo> {

    List<StoreInfo> list(Page page, @Param("qo") ScStoreQo qo);

    void saveToUserTable(@Param("storeQo") StoreQo storeQo);
}

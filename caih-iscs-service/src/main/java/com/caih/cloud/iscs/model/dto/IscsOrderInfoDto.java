package com.caih.cloud.iscs.model.dto;

import com.caih.cloud.iscs.model.entity.OrderGoodsInfo;
import com.caih.cloud.iscs.model.entity.OrderInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Kang
 * @date 2019/08/08
 */
@Data
public class IscsOrderInfoDto extends OrderInfo {
    @Valid
    @ApiModelProperty("商品信息列表")
    private List<OrderGoodsInfo> goodsList;
    @ApiModelProperty("支付完成回调地址")
    private String returnUrl;
    @NotNull(message = "验签参数不能为空")
    @ApiModelProperty("验签参数")
    private String sign;
}

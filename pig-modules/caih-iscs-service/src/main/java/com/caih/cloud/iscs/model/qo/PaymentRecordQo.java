package com.caih.cloud.iscs.model.qo;

import com.caih.cloud.iscs.model.base.BaseQo;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author Kang
 * @date 2019/10/12
 */
@Data
public class PaymentRecordQo extends BaseQo {
    //@NotBlank(message = "商户编号不能为空")
    @Length(max = 36,message = "商户号过长")
    private String cusid;

    @Length(max = 36,message = "商户编号过长")
    private String storeNo;

}

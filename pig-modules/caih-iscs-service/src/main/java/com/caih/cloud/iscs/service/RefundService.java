package com.caih.cloud.iscs.service;

import com.caih.cloud.iscs.common.Pair;
import com.caih.cloud.iscs.model.dto.RefundDto;
import com.caih.cloud.iscs.model.dto.TXRefundDto;

public interface RefundService {
    Pair<String,Object> refund(TXRefundDto dto);
}

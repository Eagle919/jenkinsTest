package com.caih.cloud.iscs.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author eagle
 * @date 2019/08/28
 */
@FeignClient("${pig.upms}")
@RequestMapping("/dict")
public interface DictFeignService {
    @GetMapping("/{status}/{type}")
    String label(@PathVariable("status") Integer status, @PathVariable("type") String type);
}

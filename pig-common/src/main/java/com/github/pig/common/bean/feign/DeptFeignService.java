package com.github.pig.common.bean.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @className DeptFeignService
 * @Author chenkang
 * @Date 2019/6/12 11:13
 * @Version 1.0
 */
@Component
@FeignClient("${pig.upms}")
@RequestMapping("/dept")
public interface DeptFeignService {
    /**
     * @param deptId deptId
     * @return UserVo
     */
    @GetMapping("/deptCode")
    String getdeptCode(String deptId);
}

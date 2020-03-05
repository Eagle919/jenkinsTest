package com.github.pig.common.bean.feign;

import com.github.pig.common.vo.UserVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 */
@Component
@FeignClient("${pig.upms}")
@RequestMapping("/user")
public interface UserFeignService {
    @GetMapping("/{id}")
    UserVO user(@PathVariable("id") Integer id);
}

package com.caih.cloud.iscs.feign;

import com.github.pig.common.vo.UserVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author zengheng
 * @date 2018/11/15 19:21
 */
@FeignClient("${pig.upms}")
@RequestMapping("/user")
public interface UserFeignService {
    @GetMapping("/{userId}")
    UserVO user(@PathVariable("userId") int userId);
}

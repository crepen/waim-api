package com.waim.api.domain.system.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.core.module.redis.service.CommonRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/system/cache")
public class SystemCacheController {

    private final CommonRedisService commonRedisService;

    @DeleteMapping("/redis")
    public ResponseEntity<?> deleteRedisCache() {

        commonRedisService.safeClearCacheSync("cache:*");

        return ResponseEntity.ok().body(
                BaseResponse.Success.builder().build()
        );
    }
}

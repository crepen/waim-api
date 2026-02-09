package com.waim.api.domain.system.controller;

import com.waim.api.common.model.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/system")
public class SystemController {


    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }
}

package com.waim.api.domain.configure.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.domain.configure.model.request.UpdateGlobalConfigRequest;
import com.waim.api.domain.configure.model.response.ConfigResponse;
import com.waim.core.domain.configure.service.GlobalConfigureService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/config/global")
public class GlobalConfigureController {

    private final GlobalConfigureService globalConfigureService;

    @GetMapping("")
    @Operation(
            summary = "Global Config Value 검색",
            description = "Global Config Value 검색"
    )
    public ResponseEntity<?> getGlobalConfig(
            @RequestParam(name = "keys") List<String> configKey
    ) {
        Map<String, String> valueList = globalConfigureService.getConfigs(configKey);


        return ResponseEntity.ok().body(
                BaseResponse.Success.builder()
                        .result(
                                valueList.entrySet()
                                        .stream()
                                        .map(entry -> {
                                            return ConfigResponse.builder()
                                                    .key(entry.getKey())
                                                    .value(entry.getValue())
                                                    .build();
                                        })
                        )
                        .build()
        );
    }


    @PostMapping("")
    public ResponseEntity<?> updateGlobalConfig(
            @RequestBody UpdateGlobalConfigRequest reqBody
    ){
        globalConfigureService.setConfig(reqBody.getKey(), reqBody.getValue() , reqBody.isEncrypt());
        return ResponseEntity.ok().body(BaseResponse.Success.builder().build());
    }
}

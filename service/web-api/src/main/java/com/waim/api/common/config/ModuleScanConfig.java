package com.waim.api.common.config;

import com.waim.module.config.crypto.CryptoConfig;
import com.waim.module.config.jasypt.JasyptConfig;
import com.waim.module.config.jwt.JwtConfig;
import com.waim.module.core.global.GlobalModuleScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Import({
        GlobalModuleScanner.class,

        CryptoConfig.class,
        JasyptConfig.class,
        JwtConfig.class
})
public class ModuleScanConfig {}

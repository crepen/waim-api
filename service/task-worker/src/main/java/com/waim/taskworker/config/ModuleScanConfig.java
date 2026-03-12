package com.waim.taskworker.config;

import com.waim.module.config.crypto.CryptoConfig;
import com.waim.module.config.jasypt.JasyptConfig;
import com.waim.module.core.domain.group.GroupModuleScanner;
import com.waim.module.core.domain.project.ProjectModuleScanner;
import com.waim.module.core.domain.task.TaskModuleScanner;
import com.waim.module.core.domain.user.UserModuleScanner;
import com.waim.module.core.system.config.SystemConfigModuleScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        CryptoConfig.class,
        JasyptConfig.class,
        TaskModuleScanner.class,
        UserModuleScanner.class,
        ProjectModuleScanner.class,
        GroupModuleScanner.class,
        SystemConfigModuleScanner.class
})
public class ModuleScanConfig {}

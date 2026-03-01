package com.waim.module.core.global;

import com.waim.module.core.domain.auth.AuthModuleScanner;
import com.waim.module.core.domain.project.ProjectModuleScanner;
import com.waim.module.core.domain.task.TaskModuleScanner;
import com.waim.module.core.domain.user.UserModuleScanner;
import com.waim.module.core.system.config.SystemConfigModuleScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        AuthModuleScanner.class,
        UserModuleScanner.class,
        TaskModuleScanner.class,
        ProjectModuleScanner.class,
        SystemConfigModuleScanner.class
})
public class GlobalModuleScanner {
}

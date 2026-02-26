package com.waim.core.common.util.date;

import java.time.LocalDateTime;

public class LocalDateTimeExtension {
    public static long toLong(LocalDateTime localDateTime){
        return DateUtil.castLocalDateTimeToLong(localDateTime);
    }
}

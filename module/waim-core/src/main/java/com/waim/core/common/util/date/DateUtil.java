package com.waim.core.common.util.date;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtil {

    public static long castLocalDateTimeToLong(LocalDateTime localDateTime){
        return localDateTime.atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }
}

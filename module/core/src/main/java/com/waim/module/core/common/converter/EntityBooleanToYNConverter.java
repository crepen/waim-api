package com.waim.module.core.common.converter;

import jakarta.persistence.AttributeConverter;

public class EntityBooleanToYNConverter  implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean aBoolean) {
        return aBoolean ? "Y" : "N";
    }

    @Override
    public Boolean convertToEntityAttribute(String s) {
        return s.equals("Y");
    }
}

package com.waim.module.storage.common.converter;

import com.waim.module.util.crypto.CryptoProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class EntityStringCryptoConverter implements AttributeConverter<String, String> {

    private final CryptoProvider cryptoProvider;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        return cryptoProvider.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return cryptoProvider.decrypt(dbData);
    }
}

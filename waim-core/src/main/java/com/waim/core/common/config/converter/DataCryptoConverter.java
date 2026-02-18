package com.waim.core.common.config.converter;

import com.waim.core.common.util.crypto.CryptoProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class DataCryptoConverter implements AttributeConverter<String, String> {

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

package com.waim.module.util.crypto;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.security.MessageDigest;

@RequiredArgsConstructor
public class CryptoProvider {

    private final TextEncryptor textEncryptor;
    private final String salt;

    public String encrypt(String text) {
        return textEncryptor.encrypt(text );
    }

    public String decrypt(String encryptedText) {
        return textEncryptor.decrypt(encryptedText);
    }

    public String staticHash(String text) {

        if (StringUtils.isBlank(text)) {
            return text;
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hash = digest.digest(text.getBytes());
            return new String(Hex.encode(hash));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
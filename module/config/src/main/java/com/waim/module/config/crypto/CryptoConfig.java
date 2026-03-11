package com.waim.module.config.crypto;

import com.waim.module.util.crypto.CryptoProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Pattern;

@Configuration
@Slf4j
@Order(0)
public class CryptoConfig {

    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]+$");


    @Value("${waim.crypto.salt:8f2d63e1a0b4c5d9}")
    private String salt;

    @Value("${waim.crypto.key:MTI0YjU2YThjMmRkNGViN2I4YWRjMGYyZDYzYmQ1Zjc=}")
    private String cryptoKey;



    @Bean
    public TextEncryptor textEncryptor(){
         return Encryptors.delux(cryptoKey, validateSalt(salt));
    }

    @Bean
    public CryptoProvider cryptoProvider(TextEncryptor textEncryptor) {
        return new CryptoProvider(textEncryptor , salt);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private String validateSalt(String configuredSalt) {
        if (configuredSalt == null || configuredSalt.isBlank()) {
            throw new IllegalStateException("waim.crypto.salt must not be blank.");
        }

        String normalizedSalt = configuredSalt.trim();
        if ((normalizedSalt.length() % 2) != 0) {
            throw new IllegalStateException("waim.crypto.salt must be a hex string with an even number of characters.");
        }

        if (!HEX_PATTERN.matcher(normalizedSalt).matches()) {
            throw new IllegalStateException("waim.crypto.salt must contain only hexadecimal characters.");
        }

        return normalizedSalt;
    }

}

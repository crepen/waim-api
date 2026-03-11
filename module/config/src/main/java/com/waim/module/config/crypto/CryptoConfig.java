package com.waim.module.config.crypto;

import com.waim.module.util.crypto.CryptoProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class CryptoConfig {

    @Value("${waim.crypto.salt:8f2d63e1a0b4c5d9}")
    private String salt;

    @Value("${waim.crypto.key:MTI0YjU2YThjMmRkNGViN2I4YWRjMGYyZDYzYmQ1Zjc=}")
    private String cryptoKey;



    @Bean
    public TextEncryptor textEncryptor(){
        log.info("================DS==================");
        log.info("SALT : {}" , salt);
        log.info("KEY : {}" , cryptoKey);
        log.info("================DS==================");

        return Encryptors.delux(cryptoKey , salt);
    }

    @Bean
    public CryptoProvider cryptoProvider(TextEncryptor textEncryptor) {
        return new CryptoProvider(textEncryptor , salt);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

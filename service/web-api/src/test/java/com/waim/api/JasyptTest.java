package com.waim.api;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("crependev")
public class JasyptTest {


    @Test
    void generate(){
        String targetText = "";
//        String password =  System.getProperty("jasypt.encryptor.password");
        String password = System.getenv("JASYPT_PWD");

        System.out.println("Read Password : " + password);

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        // 마스터 키 설정
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);


        String encryptedText = encryptor.encrypt(targetText.replace("\n" , ""));

        System.out.println("========================================");
        System.out.println("Original: " + targetText);
        System.out.println("Encrypted: ENC(" + encryptedText + ")");
        System.out.println("========================================");



        System.out.println("========================================");
        System.out.println("Encrypt String: " + encryptedText);
        System.out.println("Decrypted: " + encryptor.decrypt(encryptedText));
        System.out.println("========================================");
    }


    @Test
    void decrypt(){
        String inText = "".replace("\n" , "");
//        String password =  System.getProperty("jasypt.encryptor.password");
        String targetText = inText
                .substring(4 , inText.length() - 1);
        String password = System.getenv("JASYPT_PWD");

        System.out.println("Read Password : " + password);

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        // 마스터 키 설정
        config.setPassword(password);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);

        String decryptedText = encryptor.decrypt(targetText);

        System.out.println("========================================");
        System.out.println("Original: " + targetText.length());
        System.out.println("Encrypt String: " + targetText);
        System.out.println("Decrypted: " + decryptedText);
        System.out.println("========================================");
    }


}

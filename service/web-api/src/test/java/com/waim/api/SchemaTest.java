package com.waim.api;

import jakarta.persistence.Entity;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class SchemaTest {

    @Test
    void generateSchema() throws Exception {
//        MetadataSources metadata = new MetadataSources(
//                new StandardServiceRegistryBuilder()
//                        .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
//                        .applySetting("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy") // 스네이크 케이스 적용 시
//                        .build());
//
//        // 1. Entity 스캐너 설정
//        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
//        scanner.addIncludeFilter(new AnnotationTypeFilter(Entity.class));
//
//        // 2. 특정 패키지 내의 모든 @Entity 탐색 (패키지 경로를 본인 프로젝트에 맞게 수정)
//        String basePackage = "com.waim";
//        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
//            metadata.addAnnotatedClass(Class.forName(bd.getBeanClassName()));
//        }
//
//        // 3. SchemaExport 실행
//        SchemaExport export = new SchemaExport();
//        export.setFormat(true);
//        export.setDelimiter(";"); // 쿼리 끝 세미콜론 추가
//        export.setOutputFile("src/main/resources/generated-schema.sql");
//
//        export.createOnly(EnumSet.of(TargetType.STDOUT, TargetType.SCRIPT), metadata.buildMetadata());
    }
}

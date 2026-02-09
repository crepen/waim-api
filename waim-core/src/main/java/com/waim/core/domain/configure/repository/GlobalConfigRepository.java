package com.waim.core.domain.configure.repository;

import com.waim.core.domain.configure.model.entity.GlobalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GlobalConfigRepository extends JpaRepository<GlobalConfigEntity, String>  , JpaSpecificationExecutor<GlobalConfigEntity> {
    Optional<GlobalConfigEntity>  findByKey(String key);
    List<GlobalConfigEntity> findAllByKeyIn(List<String> keys);
}

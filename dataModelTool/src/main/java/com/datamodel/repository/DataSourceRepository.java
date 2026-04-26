package com.datamodel.repository;

import com.datamodel.entity.DataSourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataSourceRepository extends JpaRepository<DataSourceEntity, Long> {

    Optional<DataSourceEntity> findByName(String name);

    List<DataSourceEntity> findByStatus(Integer status);

    boolean existsByName(String name);
}

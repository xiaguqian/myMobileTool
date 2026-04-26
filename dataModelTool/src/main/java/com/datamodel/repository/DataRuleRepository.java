package com.datamodel.repository;

import com.datamodel.entity.DataRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataRuleRepository extends JpaRepository<DataRuleEntity, Long> {

    List<DataRuleEntity> findByDataSourceNameAndTableName(String dataSourceName, String tableName);

    Optional<DataRuleEntity> findByDataSourceNameAndTableNameAndFieldName(
            String dataSourceName, String tableName, String fieldName);

    void deleteByDataSourceNameAndTableName(String dataSourceName, String tableName);
}

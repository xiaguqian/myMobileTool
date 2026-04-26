package com.datamodel.service;

import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.dto.DataRuleDTO;
import com.datamodel.entity.DataRuleEntity;
import com.datamodel.repository.DataRuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataRuleService {

    private final DataRuleRepository dataRuleRepository;

    public DataRuleService(DataRuleRepository dataRuleRepository) {
        this.dataRuleRepository = dataRuleRepository;
    }

    public List<DataRuleDTO> listRules(String dataSourceName, String tableName) {
        try {
            DataSourceContextHolder.switchToPrimary();
            List<DataRuleEntity> entities = dataRuleRepository
                    .findByDataSourceNameAndTableName(dataSourceName, tableName);
            return entities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    public DataRuleDTO getRule(Long id) {
        try {
            DataSourceContextHolder.switchToPrimary();
            DataRuleEntity entity = dataRuleRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("规则不存在"));
            return convertToDTO(entity);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public DataRuleDTO createRule(DataRuleDTO dto) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            dataRuleRepository.findByDataSourceNameAndTableNameAndFieldName(
                    dto.getDataSourceName(), dto.getTableName(), dto.getFieldName()
            ).ifPresent(existing -> {
                throw new RuntimeException("该字段的规则已存在");
            });

            DataRuleEntity entity = convertToEntity(dto);
            entity = dataRuleRepository.save(entity);
            return convertToDTO(entity);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public DataRuleDTO updateRule(DataRuleDTO dto) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            DataRuleEntity existing = dataRuleRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("规则不存在"));

            existing.setRuleType(dto.getRuleType());
            existing.setRuleConfig(dto.getRuleConfig());
            existing.setFieldType(dto.getFieldType());
            existing.setDescription(dto.getDescription());

            existing = dataRuleRepository.save(existing);
            return convertToDTO(existing);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public void deleteRule(Long id) {
        try {
            DataSourceContextHolder.switchToPrimary();
            dataRuleRepository.deleteById(id);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public void batchSaveRules(List<DataRuleDTO> rules) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            if (rules == null || rules.isEmpty()) {
                return;
            }

            String dataSourceName = rules.get(0).getDataSourceName();
            String tableName = rules.get(0).getTableName();

            dataRuleRepository.deleteByDataSourceNameAndTableName(dataSourceName, tableName);

            for (DataRuleDTO dto : rules) {
                DataRuleEntity entity = convertToEntity(dto);
                dataRuleRepository.save(entity);
            }
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    private DataRuleDTO convertToDTO(DataRuleEntity entity) {
        DataRuleDTO dto = new DataRuleDTO();
        dto.setId(entity.getId());
        dto.setDataSourceName(entity.getDataSourceName());
        dto.setTableName(entity.getTableName());
        dto.setFieldName(entity.getFieldName());
        dto.setFieldType(entity.getFieldType());
        dto.setRuleType(entity.getRuleType());
        dto.setRuleConfig(entity.getRuleConfig());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private DataRuleEntity convertToEntity(DataRuleDTO dto) {
        DataRuleEntity entity = new DataRuleEntity();
        entity.setDataSourceName(dto.getDataSourceName());
        entity.setTableName(dto.getTableName());
        entity.setFieldName(dto.getFieldName());
        entity.setFieldType(dto.getFieldType());
        entity.setRuleType(dto.getRuleType());
        entity.setRuleConfig(dto.getRuleConfig());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

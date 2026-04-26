package com.datamodel.service;

import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.config.datasource.DynamicDataSourceManager;
import com.datamodel.dto.DataSourceDTO;
import com.datamodel.entity.DataSourceEntity;
import com.datamodel.repository.DataSourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataSourceService {

    private final DataSourceRepository dataSourceRepository;
    private final DynamicDataSourceManager dynamicDataSourceManager;

    public DataSourceService(DataSourceRepository dataSourceRepository,
                            DynamicDataSourceManager dynamicDataSourceManager) {
        this.dataSourceRepository = dataSourceRepository;
        this.dynamicDataSourceManager = dynamicDataSourceManager;
    }

    public List<DataSourceDTO> listDataSources() {
        try {
            DataSourceContextHolder.switchToPrimary();
            List<DataSourceEntity> entities = dataSourceRepository.findAll();
            return entities.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    public DataSourceDTO getDataSourceById(Long id) {
        try {
            DataSourceContextHolder.switchToPrimary();
            DataSourceEntity entity = dataSourceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("数据源不存在"));
            return convertToDTO(entity);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public DataSourceDTO createDataSource(DataSourceDTO dto) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            if (dataSourceRepository.existsByName(dto.getName())) {
                throw new RuntimeException("数据源名称已存在");
            }

            DataSourceEntity entity = convertToEntity(dto);
            entity = dataSourceRepository.save(entity);

            if (entity.getStatus() == 1) {
                addDynamicDataSource(entity);
            }

            return convertToDTO(entity);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public DataSourceDTO updateDataSource(DataSourceDTO dto) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            DataSourceEntity existing = dataSourceRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("数据源不存在"));

            if (!existing.getName().equals(dto.getName())) {
                if (dataSourceRepository.existsByName(dto.getName())) {
                    throw new RuntimeException("数据源名称已存在");
                }
            }

            boolean needRefresh = false;
            if (existing.getStatus() == 1) {
                dynamicDataSourceManager.removeDataSource(existing.getName());
                needRefresh = true;
            }

            existing.setName(dto.getName());
            existing.setDriverClassName(dto.getDriverClassName() != null ? 
                    dto.getDriverClassName() : "com.mysql.cj.jdbc.Driver");
            existing.setJdbcUrl(dto.getJdbcUrl());
            existing.setUsername(dto.getUsername());
            if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
                existing.setPassword(dto.getPassword());
            }
            existing.setDescription(dto.getDescription());
            existing.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

            existing = dataSourceRepository.save(existing);

            if (existing.getStatus() == 1) {
                addDynamicDataSource(existing);
            }

            return convertToDTO(existing);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public void deleteDataSource(Long id) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            DataSourceEntity entity = dataSourceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("数据源不存在"));

            if (entity.getStatus() == 1) {
                dynamicDataSourceManager.removeDataSource(entity.getName());
            }

            dataSourceRepository.delete(entity);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public void refreshDataSources() {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            List<DataSourceEntity> enabledDataSources = dataSourceRepository.findByStatus(1);
            
            for (DataSourceEntity entity : enabledDataSources) {
                dynamicDataSourceManager.removeDataSource(entity.getName());
                addDynamicDataSource(entity);
            }
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    private void addDynamicDataSource(DataSourceEntity entity) {
        DynamicDataSourceManager.DataSourceProperties props = 
                new DynamicDataSourceManager.DataSourceProperties();
        props.setDriverClassName(entity.getDriverClassName());
        props.setJdbcUrl(entity.getJdbcUrl());
        props.setUsername(entity.getUsername());
        props.setPassword(entity.getPassword());
        
        dynamicDataSourceManager.addDataSource(entity.getName(), props);
    }

    private DataSourceDTO convertToDTO(DataSourceEntity entity) {
        DataSourceDTO dto = new DataSourceDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDriverClassName(entity.getDriverClassName());
        dto.setJdbcUrl(entity.getJdbcUrl());
        dto.setUsername(entity.getUsername());
        dto.setPassword(null);
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    private DataSourceEntity convertToEntity(DataSourceDTO dto) {
        DataSourceEntity entity = new DataSourceEntity();
        entity.setName(dto.getName());
        entity.setDriverClassName(dto.getDriverClassName() != null ? 
                dto.getDriverClassName() : "com.mysql.cj.jdbc.Driver");
        entity.setJdbcUrl(dto.getJdbcUrl());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        return entity;
    }
}

package com.datamodel.service;

import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.dto.DataOperationDTO;
import com.datamodel.dto.DeleteDataDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.*;

@Service
public class DataOperationService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dynamicDataSource;

    public DataOperationService(JdbcTemplate jdbcTemplate, DataSource dynamicDataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dynamicDataSource = dynamicDataSource;
    }

    @Transactional
    public void upsertData(DataOperationDTO dto) {
        String dataSourceName = dto.getDataSourceName();
        String tableName = dto.getTableName();
        List<Map<String, Object>> dataList = dto.getDataList();

        if (dataList == null || dataList.isEmpty()) {
            throw new RuntimeException("数据列表不能为空");
        }

        try {
            DataSourceContextHolder.setDataSource(dataSourceName);
            
            List<String> actualPrimaryKeys = getActualPrimaryKeys(tableName);
            List<String> targetPrimaryKeys = dto.getPrimaryKeys() != null && !dto.getPrimaryKeys().isEmpty() 
                    ? dto.getPrimaryKeys() : actualPrimaryKeys;

            for (Map<String, Object> data : dataList) {
                upsertSingle(tableName, data, targetPrimaryKeys);
            }
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public void deleteData(DeleteDataDTO dto) {
        String dataSourceName = dto.getDataSourceName();
        String tableName = dto.getTableName();
        List<Map<String, Object>> dataList = dto.getDataList();

        if (dataList == null || dataList.isEmpty()) {
            throw new RuntimeException("删除数据列表不能为空");
        }

        try {
            DataSourceContextHolder.setDataSource(dataSourceName);

            List<String> actualPrimaryKeys = getActualPrimaryKeys(tableName);
            List<String> targetPrimaryKeys = dto.getPrimaryKeys() != null && !dto.getPrimaryKeys().isEmpty()
                    ? dto.getPrimaryKeys() : actualPrimaryKeys;

            for (Map<String, Object> data : dataList) {
                deleteSingle(tableName, data, targetPrimaryKeys);
            }
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    @Transactional
    public void truncateTable(String dataSourceName, String tableName) {
        try {
            DataSourceContextHolder.setDataSource(dataSourceName);
            jdbcTemplate.execute("TRUNCATE TABLE `" + tableName + "`");
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    private List<String> getActualPrimaryKeys(String tableName) {
        List<String> primaryKeys = new ArrayList<>();
        try (Connection conn = dynamicDataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getPrimaryKeys(null, null, tableName)) {
                while (rs.next()) {
                    primaryKeys.add(rs.getString("COLUMN_NAME"));
                }
            }
        } catch (Exception e) {
            // ignore, return empty list
        }
        return primaryKeys;
    }

    private void upsertSingle(String tableName, Map<String, Object> data, List<String> primaryKeys) {
        boolean exists = false;
        
        if (primaryKeys != null && !primaryKeys.isEmpty()) {
            exists = checkExists(tableName, data, primaryKeys);
        }

        if (exists) {
            updateSingle(tableName, data, primaryKeys);
        } else {
            insertSingle(tableName, data);
        }
    }

    private boolean checkExists(String tableName, Map<String, Object> data, List<String> primaryKeys) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM `").append(tableName).append("` WHERE ");
        List<Object> params = new ArrayList<>();

        for (int i = 0; i < primaryKeys.size(); i++) {
            String key = primaryKeys.get(i);
            sql.append("`").append(key).append("` = ?");
            params.add(data.get(key));
            if (i < primaryKeys.size() - 1) {
                sql.append(" AND ");
            }
        }

        Integer count = jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
        return count != null && count > 0;
    }

    private void insertSingle(String tableName, Map<String, Object> data) {
        StringBuilder sql = new StringBuilder("INSERT INTO `").append(tableName).append("` (");
        StringBuilder values = new StringBuilder(" VALUES (");
        List<Object> params = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            sql.append("`").append(entry.getKey()).append("`");
            values.append("?");
            params.add(entry.getValue());
            if (i < data.size() - 1) {
                sql.append(", ");
                values.append(", ");
            }
            i++;
        }

        sql.append(")").append(values).append(")");
        jdbcTemplate.update(sql.toString(), params.toArray());
    }

    private void updateSingle(String tableName, Map<String, Object> data, List<String> primaryKeys) {
        StringBuilder sql = new StringBuilder("UPDATE `").append(tableName).append("` SET ");
        List<Object> params = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!primaryKeys.contains(entry.getKey())) {
                sql.append("`").append(entry.getKey()).append("` = ?");
                params.add(entry.getValue());
                if (i < data.size() - primaryKeys.size()) {
                    sql.append(", ");
                }
                i++;
            }
        }

        sql.append(" WHERE ");
        for (int j = 0; j < primaryKeys.size(); j++) {
            String key = primaryKeys.get(j);
            sql.append("`").append(key).append("` = ?");
            params.add(data.get(key));
            if (j < primaryKeys.size() - 1) {
                sql.append(" AND ");
            }
        }

        jdbcTemplate.update(sql.toString(), params.toArray());
    }

    private void deleteSingle(String tableName, Map<String, Object> data, List<String> primaryKeys) {
        if (primaryKeys == null || primaryKeys.isEmpty()) {
            throw new RuntimeException("删除数据需要指定主键字段");
        }

        StringBuilder sql = new StringBuilder("DELETE FROM `").append(tableName).append("` WHERE ");
        List<Object> params = new ArrayList<>();

        for (int i = 0; i < primaryKeys.size(); i++) {
            String key = primaryKeys.get(i);
            sql.append("`").append(key).append("` = ?");
            params.add(data.get(key));
            if (i < primaryKeys.size() - 1) {
                sql.append(" AND ");
            }
        }

        jdbcTemplate.update(sql.toString(), params.toArray());
    }
}

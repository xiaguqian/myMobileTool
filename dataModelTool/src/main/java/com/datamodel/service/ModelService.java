package com.datamodel.service;

import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.dto.CreateModelDTO;
import com.datamodel.dto.TableFieldDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModelService {

    private final JdbcTemplate jdbcTemplate;

    public ModelService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void createTable(CreateModelDTO dto) {
        String dataSourceName = dto.getDataSourceName();
        String tableName = dto.getTableName();
        List<TableFieldDTO> fields = dto.getFields();

        if (fields == null || fields.isEmpty()) {
            throw new RuntimeException("字段列表不能为空");
        }

        String createSql = buildCreateTableSql(tableName, fields, dto.getTableComment());

        try {
            DataSourceContextHolder.setDataSource(dataSourceName);
            jdbcTemplate.execute(createSql);
        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    private String buildCreateTableSql(String tableName, List<TableFieldDTO> fields, String tableComment) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");

        List<String> primaryKeys = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            TableFieldDTO field = fields.get(i);
            sql.append(buildFieldDefinition(field));

            if (field.getPrimaryKey() != null && field.getPrimaryKey()) {
                primaryKeys.add(field.getFieldName());
            }

            if (i < fields.size() - 1) {
                sql.append(",");
            }
        }

        if (!primaryKeys.isEmpty()) {
            sql.append(", PRIMARY KEY (");
            for (int i = 0; i < primaryKeys.size(); i++) {
                sql.append("`").append(primaryKeys.get(i)).append("`");
                if (i < primaryKeys.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(")");
        }

        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

        if (tableComment != null && !tableComment.isEmpty()) {
            sql.append(" COMMENT='").append(escapeSql(tableComment)).append("'");
        }

        sql.append(";");

        return sql.toString();
    }

    private String buildFieldDefinition(TableFieldDTO field) {
        StringBuilder sb = new StringBuilder();
        sb.append("`").append(field.getFieldName()).append("` ");

        sb.append(buildFieldType(field));

        if (field.getNullable() == null || !field.getNullable()) {
            sb.append(" NOT NULL");
        }

        if (field.getAutoIncrement() != null && field.getAutoIncrement()) {
            sb.append(" AUTO_INCREMENT");
        }

        if (field.getDefaultValue() != null && !field.getDefaultValue().isEmpty()) {
            if ("NULL".equalsIgnoreCase(field.getDefaultValue())) {
                sb.append(" DEFAULT NULL");
            } else if ("CURRENT_TIMESTAMP".equalsIgnoreCase(field.getDefaultValue())) {
                sb.append(" DEFAULT CURRENT_TIMESTAMP");
            } else {
                sb.append(" DEFAULT '").append(escapeSql(field.getDefaultValue())).append("'");
            }
        }

        if (field.getComment() != null && !field.getComment().isEmpty()) {
            sb.append(" COMMENT '").append(escapeSql(field.getComment())).append("'");
        }

        return sb.toString();
    }

    private String buildFieldType(TableFieldDTO field) {
        String fieldType = field.getFieldType().toUpperCase();
        
        switch (fieldType) {
            case "VARCHAR":
            case "CHAR":
                int length = field.getLength() != null ? field.getLength() : 255;
                return fieldType + "(" + length + ")";
            case "DECIMAL":
            case "NUMERIC":
                int precision = field.getPrecision() != null ? field.getPrecision() : 10;
                int scale = field.getLength() != null ? field.getLength() : 2;
                return fieldType + "(" + precision + "," + scale + ")";
            case "INT":
            case "INTEGER":
            case "BIGINT":
            case "TINYINT":
            case "SMALLINT":
            case "DATE":
            case "TIME":
            case "DATETIME":
            case "TIMESTAMP":
            case "TEXT":
            case "LONGTEXT":
            case "BOOLEAN":
            case "BIT":
                return fieldType;
            default:
                return fieldType;
        }
    }

    private String escapeSql(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("'", "''");
    }
}

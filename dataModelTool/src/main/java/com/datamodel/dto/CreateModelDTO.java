package com.datamodel.dto;

import java.util.List;

public class CreateModelDTO {

    private String dataSourceName;
    private String tableName;
    private String tableComment;
    private List<TableFieldDTO> fields;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableComment() {
        return tableComment;
    }

    public void setTableComment(String tableComment) {
        this.tableComment = tableComment;
    }

    public List<TableFieldDTO> getFields() {
        return fields;
    }

    public void setFields(List<TableFieldDTO> fields) {
        this.fields = fields;
    }
}

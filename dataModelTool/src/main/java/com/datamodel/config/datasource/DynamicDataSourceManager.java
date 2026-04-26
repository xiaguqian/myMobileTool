package com.datamodel.config.datasource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DynamicDataSourceManager {

    private final DynamicDataSource dynamicDataSource;

    private final Map<String, DataSource> dynamicDataSources = new HashMap<>();

    public DynamicDataSourceManager(DynamicDataSource dynamicDataSource) {
        this.dynamicDataSource = dynamicDataSource;
    }

    public void addDataSource(String name, DataSourceProperties properties) {
        DataSource dataSource = createDataSource(properties);
        dynamicDataSources.put(name, dataSource);
        refreshTargetDataSources();
    }

    public void updateDataSource(String name, DataSourceProperties properties) {
        DataSource oldDataSource = dynamicDataSources.remove(name);
        if (oldDataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) oldDataSource).close();
            } catch (Exception e) {
                // ignore
            }
        }
        addDataSource(name, properties);
    }

    public void removeDataSource(String name) {
        DataSource dataSource = dynamicDataSources.remove(name);
        if (dataSource instanceof AutoCloseable) {
            try {
                ((AutoCloseable) dataSource).close();
            } catch (Exception e) {
                // ignore
            }
        }
        refreshTargetDataSources();
    }

    public boolean hasDataSource(String name) {
        return dynamicDataSources.containsKey(name);
    }

    private DataSource createDataSource(DataSourceProperties properties) {
        return DataSourceBuilder.create()
                .url(properties.getJdbcUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .driverClassName(properties.getDriverClassName())
                .build();
    }

    private void refreshTargetDataSources() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceContextHolder.PRIMARY_DATA_SOURCE, 
                dynamicDataSource.getResolvedDefaultDataSource());
        targetDataSources.putAll(dynamicDataSources);
        
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.afterPropertiesSet();
    }

    public static class DataSourceProperties {
        private String driverClassName;
        private String jdbcUrl;
        private String username;
        private String password;

        public String getDriverClassName() {
            return driverClassName;
        }

        public void setDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

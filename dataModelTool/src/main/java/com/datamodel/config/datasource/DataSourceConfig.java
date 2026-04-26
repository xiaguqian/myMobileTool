package com.datamodel.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.primary")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource(@Qualifier("primaryDataSource") DataSource primaryDataSource) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        
        Map<Object, Object> dataSources = new HashMap<>();
        dataSources.put(DataSourceContextHolder.PRIMARY_DATA_SOURCE, primaryDataSource);
        
        dynamicDataSource.setTargetDataSources(dataSources);
        dynamicDataSource.setDefaultTargetDataSource(primaryDataSource);
        
        return dynamicDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("dynamicDataSource") DynamicDataSource dynamicDataSource) {
        return new JdbcTemplate(dynamicDataSource);
    }
}

package com.datamodel.service;

import com.alibaba.fastjson.JSONObject;
import com.datamodel.config.datasource.DataSourceContextHolder;
import com.datamodel.entity.DataRuleEntity;
import com.datamodel.generator.DataGenerator;
import com.datamodel.repository.DataRuleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class MockDataService {

    private final DataRuleRepository dataRuleRepository;
    private final JdbcTemplate jdbcTemplate;
    private final List<DataGenerator> generators;

    private final Map<String, DataGenerator> generatorMap = new HashMap<>();

    public MockDataService(DataRuleRepository dataRuleRepository,
                           JdbcTemplate jdbcTemplate,
                           List<DataGenerator> generators) {
        this.dataRuleRepository = dataRuleRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.generators = generators;
    }

    @PostConstruct
    public void init() {
        for (DataGenerator generator : generators) {
            generatorMap.put(generator.getRuleType(), generator);
        }
    }

    @Transactional
    public int generateAndInsert(String dataSourceName, String tableName, int count, int batchSize) {
        try {
            DataSourceContextHolder.switchToPrimary();
            
            List<DataRuleEntity> rules = dataRuleRepository
                    .findByDataSourceNameAndTableName(dataSourceName, tableName);

            if (rules == null || rules.isEmpty()) {
                throw new RuntimeException("该表未配置数据生成规则");
            }

            Map<String, DataRuleEntity> ruleMap = new HashMap<>();
            for (DataRuleEntity rule : rules) {
                ruleMap.put(rule.getFieldName(), rule);
            }

            List<String> fieldNames = new ArrayList<>();
            List<DataGenerator> fieldGenerators = new ArrayList<>();
            List<JSONObject> fieldConfigs = new ArrayList<>();

            for (DataRuleEntity rule : rules) {
                fieldNames.add(rule.getFieldName());
                DataGenerator generator = generatorMap.get(rule.getRuleType());
                if (generator == null) {
                    throw new RuntimeException("不支持的规则类型: " + rule.getRuleType());
                }
                fieldGenerators.add(generator);
                String configJson = rule.getRuleConfig();
                if (configJson != null && !configJson.isEmpty()) {
                    fieldConfigs.add(JSONObject.parseObject(configJson));
                } else {
                    fieldConfigs.add(null);
                }
            }

            try {
                DataSourceContextHolder.setDataSource(dataSourceName);

                int totalInserted = 0;
                int actualBatchSize = batchSize > 0 ? batchSize : 1000;

                for (int batchStart = 0; batchStart < count; batchStart += actualBatchSize) {
                    int batchEnd = Math.min(batchStart + actualBatchSize, count);
                    List<List<Object>> batchValues = new ArrayList<>();

                    for (int i = batchStart; i < batchEnd; i++) {
                        List<Object> rowValues = new ArrayList<>();
                        for (int j = 0; j < fieldNames.size(); j++) {
                            Object value = fieldGenerators.get(j).generate(fieldConfigs.get(j), i);
                            rowValues.add(value);
                        }
                        batchValues.add(rowValues);
                    }

                    batchInsert(tableName, fieldNames, batchValues);
                    totalInserted += (batchEnd - batchStart);
                }

                return totalInserted;
            } finally {
                DataSourceContextHolder.clearDataSource();
            }

        } finally {
            DataSourceContextHolder.clearDataSource();
        }
    }

    private void batchInsert(String tableName, List<String> fieldNames, List<List<Object>> batchValues) {
        if (batchValues == null || batchValues.isEmpty()) {
            return;
        }

        StringBuilder sql = new StringBuilder("INSERT INTO `");
        sql.append(tableName).append("` (");
        for (int i = 0; i < fieldNames.size(); i++) {
            sql.append("`").append(fieldNames.get(i)).append("`");
            if (i < fieldNames.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(") VALUES ");

        StringBuilder valuePlaceholders = new StringBuilder("(");
        for (int i = 0; i < fieldNames.size(); i++) {
            valuePlaceholders.append("?");
            if (i < fieldNames.size() - 1) {
                valuePlaceholders.append(", ");
            }
        }
        valuePlaceholders.append(")");

        for (int i = 0; i < batchValues.size(); i++) {
            sql.append(valuePlaceholders);
            if (i < batchValues.size() - 1) {
                sql.append(", ");
            }
        }

        List<Object> allParams = new ArrayList<>();
        for (List<Object> row : batchValues) {
            allParams.addAll(row);
        }

        jdbcTemplate.update(sql.toString(), allParams.toArray());
    }
}

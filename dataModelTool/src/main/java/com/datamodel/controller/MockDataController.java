package com.datamodel.controller;

import com.datamodel.common.Result;
import com.datamodel.dto.MockDataDTO;
import com.datamodel.service.MockDataService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mock")
public class MockDataController {

    private final MockDataService mockDataService;

    public MockDataController(MockDataService mockDataService) {
        this.mockDataService = mockDataService;
    }

    @PostMapping("/generate")
    public Result<Map<String, Object>> generateMockData(@RequestBody MockDataDTO dto) {
        try {
            int count = dto.getCount() != null ? dto.getCount() : 100;
            int batchSize = dto.getBatchSize() != null ? dto.getBatchSize() : 1000;

            int insertedCount = mockDataService.generateAndInsert(
                    dto.getDataSourceName(),
                    dto.getTableName(),
                    count,
                    batchSize
            );

            Map<String, Object> result = new HashMap<>();
            result.put("count", insertedCount);
            result.put("dataSourceName", dto.getDataSourceName());
            result.put("tableName", dto.getTableName());

            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

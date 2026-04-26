package com.datamodel.controller;

import com.datamodel.common.Result;
import com.datamodel.dto.DataOperationDTO;
import com.datamodel.dto.DeleteDataDTO;
import com.datamodel.service.DataOperationService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataOperationController {

    private final DataOperationService dataOperationService;

    public DataOperationController(DataOperationService dataOperationService) {
        this.dataOperationService = dataOperationService;
    }

    @PostMapping("/upsert")
    public Result<Void> upsertData(@RequestBody DataOperationDTO dto) {
        try {
            dataOperationService.upsertData(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public Result<Void> deleteData(@RequestBody DeleteDataDTO dto) {
        try {
            dataOperationService.deleteData(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/truncate")
    public Result<Void> truncateTable(@RequestBody Map<String, String> params) {
        try {
            String dataSourceName = params.get("dataSourceName");
            String tableName = params.get("tableName");
            dataOperationService.truncateTable(dataSourceName, tableName);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

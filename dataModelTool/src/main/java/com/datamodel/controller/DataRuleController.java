package com.datamodel.controller;

import com.datamodel.common.Result;
import com.datamodel.dto.DataRuleDTO;
import com.datamodel.service.DataRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rule")
public class DataRuleController {

    private final DataRuleService dataRuleService;

    public DataRuleController(DataRuleService dataRuleService) {
        this.dataRuleService = dataRuleService;
    }

    @GetMapping("/list")
    public Result<List<DataRuleDTO>> listRules(
            @RequestParam String dataSourceName,
            @RequestParam String tableName) {
        try {
            List<DataRuleDTO> list = dataRuleService.listRules(dataSourceName, tableName);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<DataRuleDTO> getRule(@PathVariable Long id) {
        try {
            DataRuleDTO dto = dataRuleService.getRule(id);
            return Result.success(dto);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/create")
    public Result<DataRuleDTO> createRule(@RequestBody DataRuleDTO dto) {
        try {
            DataRuleDTO result = dataRuleService.createRule(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<DataRuleDTO> updateRule(@RequestBody DataRuleDTO dto) {
        try {
            DataRuleDTO result = dataRuleService.updateRule(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    public Result<Void> deleteRule(@PathVariable Long id) {
        try {
            dataRuleService.deleteRule(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/batchSave")
    public Result<Void> batchSaveRules(@RequestBody List<DataRuleDTO> rules) {
        try {
            dataRuleService.batchSaveRules(rules);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

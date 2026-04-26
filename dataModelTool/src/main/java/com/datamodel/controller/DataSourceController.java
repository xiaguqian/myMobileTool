package com.datamodel.controller;

import com.datamodel.common.Result;
import com.datamodel.dto.DataSourceDTO;
import com.datamodel.service.DataSourceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/datasource")
public class DataSourceController {

    private final DataSourceService dataSourceService;

    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @GetMapping("/list")
    public Result<List<DataSourceDTO>> listDataSources() {
        try {
            List<DataSourceDTO> list = dataSourceService.listDataSources();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<DataSourceDTO> getDataSource(@PathVariable Long id) {
        try {
            DataSourceDTO dto = dataSourceService.getDataSourceById(id);
            return Result.success(dto);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/create")
    public Result<DataSourceDTO> createDataSource(@RequestBody DataSourceDTO dto) {
        try {
            DataSourceDTO result = dataSourceService.createDataSource(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/update")
    public Result<DataSourceDTO> updateDataSource(@RequestBody DataSourceDTO dto) {
        try {
            DataSourceDTO result = dataSourceService.updateDataSource(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/delete/{id}")
    public Result<Void> deleteDataSource(@PathVariable Long id) {
        try {
            dataSourceService.deleteDataSource(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public Result<Void> refreshDataSources() {
        try {
            dataSourceService.refreshDataSources();
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

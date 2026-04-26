package com.datamodel.controller;

import com.datamodel.common.Result;
import com.datamodel.dto.CreateModelDTO;
import com.datamodel.service.ModelService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/model")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping("/create")
    public Result<Void> createModel(@RequestBody CreateModelDTO dto) {
        try {
            modelService.createTable(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

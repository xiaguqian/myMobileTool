package com.facemenu.user.controller;

import com.facemenu.user.dto.ApiResponse;
import com.facemenu.user.entity.Menu;
import com.facemenu.user.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slot")
@Tag(name = "栏位接口", description = "用户端栏位相关的API接口")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @GetMapping("/{slotCode}/pages")
    @Operation(summary = "根据栏位号获取页面信息", description = "获取指定栏位关联的所有页面及其组件配置")
    public ApiResponse<List<Map<String, Object>>> getPagesBySlotCode(
            @Parameter(description = "栏位号") @PathVariable String slotCode) {
        List<Map<String, Object>> pages = slotService.getPagesBySlotCode(slotCode);
        return ApiResponse.success(pages);
    }

    @GetMapping("/{slotCode}/menu-trees")
    @Operation(summary = "根据栏位号获取菜单树", description = "获取指定栏位关联的所有菜单树及其菜单结构")
    public ApiResponse<List<Map<String, Object>>> getMenuTreesBySlotCode(
            @Parameter(description = "栏位号") @PathVariable String slotCode) {
        List<Map<String, Object>> menuTrees = slotService.getMenuTreesBySlotCode(slotCode);
        return ApiResponse.success(menuTrees);
    }

    @GetMapping("/{slotCode}")
    @Operation(summary = "根据栏位号获取完整信息", description = "获取指定栏位的完整信息，包括页面和菜单树")
    public ApiResponse<Map<String, Object>> getSlotInfo(
            @Parameter(description = "栏位号") @PathVariable String slotCode) {
        List<Map<String, Object>> pages = slotService.getPagesBySlotCode(slotCode);
        List<Map<String, Object>> menuTrees = slotService.getMenuTreesBySlotCode(slotCode);
        
        Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("slotCode", slotCode);
        result.put("pages", pages);
        result.put("menuTrees", menuTrees);
        
        return ApiResponse.success(result);
    }

    @GetMapping("/menus/all")
    @Operation(summary = "获取全量菜单", description = "获取所有启用状态的菜单列表（用于权限判断）")
    public ApiResponse<List<Menu>> getAllMenus() {
        List<Menu> menus = slotService.getAllMenus();
        return ApiResponse.success(menus);
    }
}
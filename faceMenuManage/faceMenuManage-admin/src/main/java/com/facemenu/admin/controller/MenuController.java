package com.facemenu.admin.controller;

import com.facemenu.admin.entity.Menu;
import com.facemenu.admin.entity.MenuTree;
import com.facemenu.admin.service.MenuService;
import com.facemenu.admin.service.MenuTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuTreeService menuTreeService;

    @GetMapping("/list")
    public String list(@RequestParam(required = false) Long treeId, Model model) {
        List<MenuTree> menuTrees = menuTreeService.findAll();
        model.addAttribute("menuTrees", menuTrees);
        
        if (treeId != null) {
            List<Map<String, Object>> menuTree = buildMenuTreeWithNames(treeId);
            model.addAttribute("menuTree", menuTree);
            model.addAttribute("selectedTreeId", treeId);
            Optional<MenuTree> selectedTree = menuTreeService.findById(treeId);
            selectedTree.ifPresent(menuTree1 -> model.addAttribute("selectedTree", menuTree1));
        }
        
        return "menu/list";
    }

    private List<Map<String, Object>> buildMenuTreeWithNames(Long treeId) {
        List<Menu> allMenus = menuService.findByTreeId(treeId);
        Map<Long, List<Menu>> parentMap = new HashMap<>();
        List<Menu> rootMenus = new java.util.ArrayList<>();

        for (Menu menu : allMenus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootMenus.add(menu);
            } else {
                parentMap.computeIfAbsent(menu.getParentId(), k -> new java.util.ArrayList<>()).add(menu);
            }
        }

        return buildTreeNodes(rootMenus, parentMap);
    }

    private List<Map<String, Object>> buildTreeNodes(List<Menu> menus, Map<Long, List<Menu>> parentMap) {
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Menu menu : menus) {
            Map<String, Object> node = new java.util.LinkedHashMap<>();
            node.put("id", menu.getId());
            node.put("menuCode", menu.getMenuCode());
            node.put("menuName", menu.getMenuName());
            node.put("menuUrl", menu.getMenuUrl());
            node.put("icon", menu.getIcon());
            node.put("sortOrder", menu.getSortOrder());
            node.put("parentId", menu.getParentId());
            node.put("status", menu.getStatus());
            node.put("permission", menu.getPermission());

            List<Menu> children = parentMap.get(menu.getId());
            if (children != null && !children.isEmpty()) {
                node.put("children", buildTreeNodes(children, parentMap));
            }
            result.add(node);
        }
        return result;
    }

    @GetMapping("/add")
    public String addForm(@RequestParam(required = false) Long treeId, 
                          @RequestParam(required = false) Long parentId,
                          Model model) {
        List<MenuTree> menuTrees = menuTreeService.findAll();
        model.addAttribute("menuTrees", menuTrees);
        
        Menu menu = new Menu();
        if (treeId != null) {
            menu.setTreeId(treeId);
        }
        if (parentId != null) {
            menu.setParentId(parentId);
        }
        model.addAttribute("menu", menu);
        
        if (treeId != null) {
            List<Menu> menus = menuService.findByTreeId(treeId);
            model.addAttribute("availableParents", menus);
        }
        
        return "menu/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Menu> menuOpt = menuService.findById(id);
        if (menuOpt.isPresent()) {
            Menu menu = menuOpt.get();
            model.addAttribute("menu", menu);
            
            List<MenuTree> menuTrees = menuTreeService.findAll();
            model.addAttribute("menuTrees", menuTrees);
            
            if (menu.getTreeId() != null) {
                List<Menu> menus = menuService.findByTreeId(menu.getTreeId());
                menus.removeIf(m -> m.getId().equals(menu.getId()));
                model.addAttribute("availableParents", menus);
            }
            
            return "menu/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "菜单不存在");
            return "redirect:/menu/list";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Menu menu, RedirectAttributes redirectAttributes) {
        try {
            menuService.save(menu);
            redirectAttributes.addFlashAttribute("success", "保存成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "保存失败: " + e.getMessage());
        }
        return "redirect:/menu/list?treeId=" + (menu.getTreeId() != null ? menu.getTreeId() : "");
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Menu> menuOpt = menuService.findById(id);
        Long treeId = null;
        if (menuOpt.isPresent()) {
            treeId = menuOpt.get().getTreeId();
        }
        
        try {
            menuService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/menu/list" + (treeId != null ? "?treeId=" + treeId : "");
    }
}
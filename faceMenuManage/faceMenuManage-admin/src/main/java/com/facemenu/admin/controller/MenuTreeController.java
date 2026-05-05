package com.facemenu.admin.controller;

import com.facemenu.admin.entity.MenuTree;
import com.facemenu.admin.service.MenuTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/menu-tree")
public class MenuTreeController {

    @Autowired
    private MenuTreeService menuTreeService;

    @GetMapping("/list")
    public String list(Model model) {
        List<MenuTree> menuTrees = menuTreeService.findAll();
        model.addAttribute("menuTrees", menuTrees);
        return "menu-tree/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("menuTree", new MenuTree());
        return "menu-tree/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<MenuTree> menuTreeOpt = menuTreeService.findById(id);
        if (menuTreeOpt.isPresent()) {
            model.addAttribute("menuTree", menuTreeOpt.get());
            return "menu-tree/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "菜单树不存在");
            return "redirect:/menu-tree/list";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute MenuTree menuTree, RedirectAttributes redirectAttributes) {
        try {
            menuTreeService.save(menuTree);
            redirectAttributes.addFlashAttribute("success", "保存成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "保存失败: " + e.getMessage());
        }
        return "redirect:/menu-tree/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            menuTreeService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/menu-tree/list";
    }
}
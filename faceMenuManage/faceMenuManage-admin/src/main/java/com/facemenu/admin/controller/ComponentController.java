package com.facemenu.admin.controller;

import com.facemenu.admin.entity.Component;
import com.facemenu.admin.service.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/component")
public class ComponentController {

    @Autowired
    private ComponentService componentService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Component> components = componentService.findAll();
        model.addAttribute("components", components);
        return "component/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("component", new Component());
        return "component/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Component> componentOpt = componentService.findById(id);
        if (componentOpt.isPresent()) {
            model.addAttribute("component", componentOpt.get());
            return "component/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "组件不存在");
            return "redirect:/component/list";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Component component, RedirectAttributes redirectAttributes) {
        try {
            componentService.save(component);
            redirectAttributes.addFlashAttribute("success", "保存成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "保存失败: " + e.getMessage());
        }
        return "redirect:/component/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            componentService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/component/list";
    }
}
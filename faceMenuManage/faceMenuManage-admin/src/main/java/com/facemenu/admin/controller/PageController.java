package com.facemenu.admin.controller;

import com.facemenu.admin.entity.Page;
import com.facemenu.admin.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Page> pages = pageService.findAll();
        model.addAttribute("pages", pages);
        return "page/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("page", new Page());
        return "page/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Page> pageOpt = pageService.findById(id);
        if (pageOpt.isPresent()) {
            model.addAttribute("page", pageOpt.get());
            return "page/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "页面不存在");
            return "redirect:/page/list";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Page page, RedirectAttributes redirectAttributes) {
        try {
            pageService.save(page);
            redirectAttributes.addFlashAttribute("success", "保存成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "保存失败: " + e.getMessage());
        }
        return "redirect:/page/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pageService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/page/list";
    }
}
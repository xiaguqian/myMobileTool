package com.facemenu.admin.controller;

import com.facemenu.admin.entity.Slot;
import com.facemenu.admin.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/slot")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Slot> slots = slotService.findAll();
        model.addAttribute("slots", slots);
        return "slot/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("slot", new Slot());
        return "slot/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Slot> slotOpt = slotService.findById(id);
        if (slotOpt.isPresent()) {
            model.addAttribute("slot", slotOpt.get());
            return "slot/form";
        } else {
            redirectAttributes.addFlashAttribute("error", "栏位不存在");
            return "redirect:/slot/list";
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Slot slot, RedirectAttributes redirectAttributes) {
        try {
            slotService.save(slot);
            redirectAttributes.addFlashAttribute("success", "保存成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "保存失败: " + e.getMessage());
        }
        return "redirect:/slot/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            slotService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "删除成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "删除失败: " + e.getMessage());
        }
        return "redirect:/slot/list";
    }
}
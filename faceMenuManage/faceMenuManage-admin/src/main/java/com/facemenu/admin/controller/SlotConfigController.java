package com.facemenu.admin.controller;

import com.facemenu.admin.entity.*;
import com.facemenu.admin.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/slot-config")
public class SlotConfigController {

    @Autowired
    private SlotService slotService;

    @Autowired
    private MenuTreeService menuTreeService;

    @Autowired
    private PageService pageService;

    @Autowired
    private SlotMenuTreeService slotMenuTreeService;

    @Autowired
    private SlotPageService slotPageService;

    @GetMapping("/menu-tree/{slotId}")
    public String menuTreeConfig(@PathVariable Long slotId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Slot> slotOpt = slotService.findById(slotId);
        if (!slotOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "栏位不存在");
            return "redirect:/slot/list";
        }
        
        model.addAttribute("slot", slotOpt.get());
        
        List<MenuTree> allMenuTrees = menuTreeService.findAll();
        model.addAttribute("allMenuTrees", allMenuTrees);
        
        List<Map<String, Object>> linkedMenuTrees = slotMenuTreeService.findSlotMenuTreesWithDetails(slotId);
        model.addAttribute("linkedMenuTrees", linkedMenuTrees);
        
        return "slot-config/menu-tree";
    }

    @PostMapping("/menu-tree/add")
    public String addMenuTree(@RequestParam Long slotId, 
                              @RequestParam Long treeId,
                              @RequestParam(defaultValue = "0") Integer sortOrder,
                              RedirectAttributes redirectAttributes) {
        try {
            SlotMenuTree slotMenuTree = new SlotMenuTree();
            slotMenuTree.setSlotId(slotId);
            slotMenuTree.setTreeId(treeId);
            slotMenuTree.setSortOrder(sortOrder);
            slotMenuTreeService.save(slotMenuTree);
            redirectAttributes.addFlashAttribute("success", "关联成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "关联失败: " + e.getMessage());
        }
        return "redirect:/slot-config/menu-tree/" + slotId;
    }

    @GetMapping("/menu-tree/delete/{id}")
    public String deleteMenuTree(@PathVariable Long id, 
                                 @RequestParam Long slotId,
                                 RedirectAttributes redirectAttributes) {
        try {
            slotMenuTreeService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "取消关联成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "取消关联失败: " + e.getMessage());
        }
        return "redirect:/slot-config/menu-tree/" + slotId;
    }

    @GetMapping("/page/{slotId}")
    public String pageConfig(@PathVariable Long slotId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Slot> slotOpt = slotService.findById(slotId);
        if (!slotOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "栏位不存在");
            return "redirect:/slot/list";
        }
        
        model.addAttribute("slot", slotOpt.get());
        
        List<Page> allPages = pageService.findAll();
        model.addAttribute("allPages", allPages);
        
        List<Map<String, Object>> linkedPages = slotPageService.findSlotPagesWithDetails(slotId);
        model.addAttribute("linkedPages", linkedPages);
        
        return "slot-config/page";
    }

    @PostMapping("/page/add")
    public String addPage(@RequestParam Long slotId, 
                          @RequestParam Long pageId,
                          @RequestParam(defaultValue = "0") Integer sortOrder,
                          RedirectAttributes redirectAttributes) {
        try {
            SlotPage slotPage = new SlotPage();
            slotPage.setSlotId(slotId);
            slotPage.setPageId(pageId);
            slotPage.setSortOrder(sortOrder);
            slotPageService.save(slotPage);
            redirectAttributes.addFlashAttribute("success", "关联成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "关联失败: " + e.getMessage());
        }
        return "redirect:/slot-config/page/" + slotId;
    }

    @GetMapping("/page/delete/{id}")
    public String deletePage(@PathVariable Long id, 
                             @RequestParam Long slotId,
                             RedirectAttributes redirectAttributes) {
        try {
            slotPageService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "取消关联成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "取消关联失败: " + e.getMessage());
        }
        return "redirect:/slot-config/page/" + slotId;
    }
}
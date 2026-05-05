package com.facemenu.admin.controller;

import com.facemenu.admin.entity.Component;
import com.facemenu.admin.entity.Page;
import com.facemenu.admin.entity.PageComponent;
import com.facemenu.admin.service.ComponentService;
import com.facemenu.admin.service.PageComponentService;
import com.facemenu.admin.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/page-config")
public class PageConfigController {

    @Autowired
    private PageService pageService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private PageComponentService pageComponentService;

    @GetMapping("/component/{pageId}")
    public String componentConfig(@PathVariable Long pageId, Model model, RedirectAttributes redirectAttributes) {
        Optional<Page> pageOpt = pageService.findById(pageId);
        if (!pageOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "页面不存在");
            return "redirect:/page/list";
        }
        
        model.addAttribute("page", pageOpt.get());
        
        List<Component> allComponents = componentService.findAll();
        model.addAttribute("allComponents", allComponents);
        
        List<Map<String, Object>> linkedComponents = pageComponentService.findPageComponentsWithDetails(pageId);
        model.addAttribute("linkedComponents", linkedComponents);
        
        return "page-config/component";
    }

    @PostMapping("/component/add")
    public String addComponent(@RequestParam Long pageId, 
                               @RequestParam Long componentId,
                               @RequestParam(defaultValue = "0") Integer sortOrder,
                               @RequestParam(required = false) String configOverride,
                               RedirectAttributes redirectAttributes) {
        try {
            PageComponent pageComponent = new PageComponent();
            pageComponent.setPageId(pageId);
            pageComponent.setComponentId(componentId);
            pageComponent.setSortOrder(sortOrder);
            pageComponent.setConfigOverride(configOverride);
            pageComponentService.save(pageComponent);
            redirectAttributes.addFlashAttribute("success", "关联成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "关联失败: " + e.getMessage());
        }
        return "redirect:/page-config/component/" + pageId;
    }

    @GetMapping("/component/delete/{id}")
    public String deleteComponent(@PathVariable Long id, 
                                  @RequestParam Long pageId,
                                  RedirectAttributes redirectAttributes) {
        try {
            pageComponentService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "取消关联成功");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "取消关联失败: " + e.getMessage());
        }
        return "redirect:/page-config/component/" + pageId;
    }

    @PostMapping("/component/update/{id}")
    public String updateComponent(@PathVariable Long id,
                                  @RequestParam Long pageId,
                                  @RequestParam Integer sortOrder,
                                  @RequestParam(required = false) String configOverride,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<PageComponent> pcOpt = java.util.Optional.empty();
            List<PageComponent> pcs = pageComponentService.findByPageId(pageId);
            for (PageComponent pc : pcs) {
                if (pc.getId().equals(id)) {
                    pcOpt = java.util.Optional.of(pc);
                    break;
                }
            }
            
            if (pcOpt.isPresent()) {
                PageComponent pc = pcOpt.get();
                pc.setSortOrder(sortOrder);
                pc.setConfigOverride(configOverride);
                pageComponentService.save(pc);
                redirectAttributes.addFlashAttribute("success", "更新成功");
            } else {
                redirectAttributes.addFlashAttribute("error", "关联记录不存在");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新失败: " + e.getMessage());
        }
        return "redirect:/page-config/component/" + pageId;
    }
}
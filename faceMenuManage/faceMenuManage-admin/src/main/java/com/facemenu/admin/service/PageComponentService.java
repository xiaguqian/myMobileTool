package com.facemenu.admin.service;

import com.facemenu.admin.entity.Component;
import com.facemenu.admin.entity.Page;
import com.facemenu.admin.entity.PageComponent;
import com.facemenu.admin.repository.ComponentRepository;
import com.facemenu.admin.repository.PageComponentRepository;
import com.facemenu.admin.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PageComponentService {

    @Autowired
    private PageComponentRepository pageComponentRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ComponentRepository componentRepository;

    public List<PageComponent> findByPageId(Long pageId) {
        return pageComponentRepository.findByPageIdOrderBySortOrder(pageId);
    }

    public List<Map<String, Object>> findPageComponentsWithDetails(Long pageId) {
        List<PageComponent> pageComponents = pageComponentRepository.findByPageIdOrderBySortOrder(pageId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (PageComponent pc : pageComponents) {
            Optional<Component> componentOpt = componentRepository.findById(pc.getComponentId());
            if (componentOpt.isPresent()) {
                Component component = componentOpt.get();
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", pc.getId());
                item.put("pageId", pc.getPageId());
                item.put("componentId", component.getId());
                item.put("componentCode", component.getComponentCode());
                item.put("componentName", component.getComponentName());
                item.put("componentType", component.getComponentType());
                item.put("configJson", pc.getConfigOverride() != null ? pc.getConfigOverride() : component.getConfigJson());
                item.put("sortOrder", pc.getSortOrder());
                result.add(item);
            }
        }
        return result;
    }

    @Transactional
    public PageComponent save(PageComponent pageComponent) {
        Optional<PageComponent> existing = pageComponentRepository.findByPageIdAndComponentId(
                pageComponent.getPageId(), pageComponent.getComponentId());
        
        if (existing.isPresent() && (pageComponent.getId() == null || !existing.get().getId().equals(pageComponent.getId()))) {
            throw new RuntimeException("该页面已关联此组件");
        }
        
        return pageComponentRepository.save(pageComponent);
    }

    @Transactional
    public void deleteById(Long id) {
        pageComponentRepository.deleteById(id);
    }

    @Transactional
    public void deleteByPageIdAndComponentId(Long pageId, Long componentId) {
        pageComponentRepository.deleteByPageIdAndComponentId(pageId, componentId);
    }

    @Transactional
    public void updatePageComponents(Long pageId, List<PageComponent> components) {
        pageComponentRepository.deleteByPageId(pageId);
        for (PageComponent pc : components) {
            pc.setPageId(pageId);
            pc.setId(null);
            pageComponentRepository.save(pc);
        }
    }
}
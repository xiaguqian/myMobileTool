package com.facemenu.user.service;

import com.facemenu.user.entity.*;
import com.facemenu.user.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private SlotMenuTreeRepository slotMenuTreeRepository;

    @Autowired
    private SlotPageRepository slotPageRepository;

    @Autowired
    private MenuTreeRepository menuTreeRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageComponentRepository pageComponentRepository;

    @Autowired
    private ComponentRepository componentRepository;

    public Optional<Slot> findBySlotCode(String slotCode) {
        return slotRepository.findBySlotCodeAndStatus(slotCode, 1);
    }

    public List<Map<String, Object>> getMenuTreesBySlotCode(String slotCode) {
        Optional<Slot> slotOpt = slotRepository.findBySlotCodeAndStatus(slotCode, 1);
        if (!slotOpt.isPresent()) {
            return new ArrayList<>();
        }

        Slot slot = slotOpt.get();
        List<SlotMenuTree> slotMenuTrees = slotMenuTreeRepository.findBySlotIdOrderBySortOrder(slot.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (SlotMenuTree smt : slotMenuTrees) {
            Optional<MenuTree> menuTreeOpt = menuTreeRepository.findByIdAndStatus(smt.getTreeId(), 1);
            if (menuTreeOpt.isPresent()) {
                MenuTree menuTree = menuTreeOpt.get();
                Map<String, Object> treeData = new LinkedHashMap<>();
                treeData.put("id", menuTree.getId());
                treeData.put("treeCode", menuTree.getTreeCode());
                treeData.put("treeName", menuTree.getTreeName());
                treeData.put("sortOrder", smt.getSortOrder());
                
                List<Menu> menus = menuRepository.findByTreeIdAndStatusOrderBySortOrder(menuTree.getId(), 1);
                treeData.put("menus", buildMenuTree(menus));
                
                result.add(treeData);
            }
        }
        return result;
    }

    public List<Map<String, Object>> getPagesBySlotCode(String slotCode) {
        Optional<Slot> slotOpt = slotRepository.findBySlotCodeAndStatus(slotCode, 1);
        if (!slotOpt.isPresent()) {
            return new ArrayList<>();
        }

        Slot slot = slotOpt.get();
        List<SlotPage> slotPages = slotPageRepository.findBySlotIdOrderBySortOrder(slot.getId());
        List<Map<String, Object>> result = new ArrayList<>();

        for (SlotPage sp : slotPages) {
            Optional<Page> pageOpt = pageRepository.findByIdAndStatus(sp.getPageId(), 1);
            if (pageOpt.isPresent()) {
                Page page = pageOpt.get();
                Map<String, Object> pageData = new LinkedHashMap<>();
                pageData.put("id", page.getId());
                pageData.put("pageCode", page.getPageCode());
                pageData.put("pageName", page.getPageName());
                pageData.put("routePath", page.getRoutePath());
                pageData.put("sortOrder", sp.getSortOrder());
                
                List<PageComponent> pageComponents = pageComponentRepository.findByPageIdOrderBySortOrder(page.getId());
                List<Map<String, Object>> components = new ArrayList<>();
                
                for (PageComponent pc : pageComponents) {
                    Optional<Component> componentOpt = componentRepository.findByIdAndStatus(pc.getComponentId(), 1);
                    if (componentOpt.isPresent()) {
                        Component component = componentOpt.get();
                        Map<String, Object> compData = new LinkedHashMap<>();
                        compData.put("id", component.getId());
                        compData.put("componentCode", component.getComponentCode());
                        compData.put("componentName", component.getComponentName());
                        compData.put("componentType", component.getComponentType());
                        
                        String configJson = pc.getConfigOverride() != null ? pc.getConfigOverride() : component.getConfigJson();
                        if (configJson != null && !configJson.isEmpty()) {
                            try {
                                compData.put("config", com.alibaba.fastjson.JSON.parse(configJson));
                            } catch (Exception e) {
                                compData.put("config", configJson);
                            }
                        } else {
                            compData.put("config", new LinkedHashMap<>());
                        }
                        compData.put("sortOrder", pc.getSortOrder());
                        components.add(compData);
                    }
                }
                pageData.put("components", components);
                result.add(pageData);
            }
        }
        return result;
    }

    public List<Menu> getAllMenus() {
        return menuRepository.findByStatusOrderBySortOrder(1);
    }

    private List<Map<String, Object>> buildMenuTree(List<Menu> menus) {
        Map<Long, List<Menu>> parentMap = new HashMap<>();
        List<Menu> rootMenus = new ArrayList<>();

        for (Menu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootMenus.add(menu);
            } else {
                parentMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
            }
        }

        return buildTreeNodes(rootMenus, parentMap);
    }

    private List<Map<String, Object>> buildTreeNodes(List<Menu> menus, Map<Long, List<Menu>> parentMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Menu menu : menus) {
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", menu.getId());
            node.put("menuCode", menu.getMenuCode());
            node.put("menuName", menu.getMenuName());
            node.put("menuUrl", menu.getMenuUrl());
            node.put("icon", menu.getIcon());
            node.put("sortOrder", menu.getSortOrder());
            node.put("permission", menu.getPermission());

            List<Menu> children = parentMap.get(menu.getId());
            if (children != null && !children.isEmpty()) {
                node.put("children", buildTreeNodes(children, parentMap));
            }
            result.add(node);
        }
        return result;
    }
}
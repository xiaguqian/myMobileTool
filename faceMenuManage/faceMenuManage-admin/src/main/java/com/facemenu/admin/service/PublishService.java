package com.facemenu.admin.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.facemenu.admin.entity.*;
import com.facemenu.admin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PublishService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuTreeRepository menuTreeRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private PageComponentRepository pageComponentRepository;

    @Autowired
    private SlotMenuTreeRepository slotMenuTreeRepository;

    @Autowired
    private SlotPageRepository slotPageRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Transactional(readOnly = true)
    public Map<String, Object> generateAllData() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("publishTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Map<String, Object> slotsData = new LinkedHashMap<>();
        List<Slot> slots = slotRepository.findAll();
        for (Slot slot : slots) {
            Map<String, Object> slotData = new LinkedHashMap<>();
            slotData.put("id", slot.getId());
            slotData.put("slotCode", slot.getSlotCode());
            slotData.put("slotName", slot.getSlotName());
            slotData.put("description", slot.getDescription());
            slotData.put("status", slot.getStatus());

            List<Map<String, Object>> menuTrees = new ArrayList<>();
            List<SlotMenuTree> slotMenuTrees = slotMenuTreeRepository.findBySlotIdOrderBySortOrder(slot.getId());
            for (SlotMenuTree smt : slotMenuTrees) {
                Optional<MenuTree> menuTreeOpt = menuTreeRepository.findById(smt.getTreeId());
                if (menuTreeOpt.isPresent()) {
                    MenuTree menuTree = menuTreeOpt.get();
                    Map<String, Object> treeData = new LinkedHashMap<>();
                    treeData.put("id", menuTree.getId());
                    treeData.put("treeCode", menuTree.getTreeCode());
                    treeData.put("treeName", menuTree.getTreeName());
                    treeData.put("sortOrder", smt.getSortOrder());
                    treeData.put("menus", buildMenuTreeStructure(menuTree.getId()));
                    menuTrees.add(treeData);
                }
            }
            slotData.put("menuTrees", menuTrees);

            List<Map<String, Object>> pages = new ArrayList<>();
            List<SlotPage> slotPages = slotPageRepository.findBySlotIdOrderBySortOrder(slot.getId());
            for (SlotPage sp : slotPages) {
                Optional<Page> pageOpt = pageRepository.findById(sp.getPageId());
                if (pageOpt.isPresent()) {
                    Page page = pageOpt.get();
                    Map<String, Object> pageData = new LinkedHashMap<>();
                    pageData.put("id", page.getId());
                    pageData.put("pageCode", page.getPageCode());
                    pageData.put("pageName", page.getPageName());
                    pageData.put("routePath", page.getRoutePath());
                    pageData.put("sortOrder", sp.getSortOrder());
                    pageData.put("components", getPageComponents(page.getId()));
                    pages.add(pageData);
                }
            }
            slotData.put("pages", pages);

            slotsData.put(slot.getSlotCode(), slotData);
        }
        result.put("slots", slotsData);

        List<Map<String, Object>> allMenus = new ArrayList<>();
        List<Menu> menus = menuRepository.findAll();
        for (Menu menu : menus) {
            Map<String, Object> menuData = new LinkedHashMap<>();
            menuData.put("id", menu.getId());
            menuData.put("menuCode", menu.getMenuCode());
            menuData.put("menuName", menu.getMenuName());
            menuData.put("menuUrl", menu.getMenuUrl());
            menuData.put("icon", menu.getIcon());
            menuData.put("sortOrder", menu.getSortOrder());
            menuData.put("parentId", menu.getParentId());
            menuData.put("treeId", menu.getTreeId());
            menuData.put("status", menu.getStatus());
            menuData.put("permission", menu.getPermission());
            allMenus.add(menuData);
        }
        result.put("allMenus", allMenus);

        return result;
    }

    private List<Map<String, Object>> buildMenuTreeStructure(Long treeId) {
        List<Menu> allMenus = menuRepository.findAllByTreeIdOrdered(treeId);
        Map<Long, List<Menu>> parentMap = new HashMap<>();
        List<Menu> rootMenus = new ArrayList<>();

        for (Menu menu : allMenus) {
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

    private List<Map<String, Object>> getPageComponents(Long pageId) {
        List<PageComponent> pageComponents = pageComponentRepository.findByPageIdOrderBySortOrder(pageId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (PageComponent pc : pageComponents) {
            Optional<Component> componentOpt = componentRepository.findById(pc.getComponentId());
            if (componentOpt.isPresent()) {
                Component component = componentOpt.get();
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", component.getId());
                item.put("componentCode", component.getComponentCode());
                item.put("componentName", component.getComponentName());
                item.put("componentType", component.getComponentType());
                
                String configJson = pc.getConfigOverride() != null ? pc.getConfigOverride() : component.getConfigJson();
                if (configJson != null && !configJson.isEmpty()) {
                    try {
                        item.put("config", JSON.parse(configJson));
                    } catch (Exception e) {
                        item.put("config", configJson);
                    }
                } else {
                    item.put("config", new LinkedHashMap<>());
                }
                item.put("sortOrder", pc.getSortOrder());
                result.add(item);
            }
        }
        return result;
    }

    public File generateJsonFile() throws IOException {
        Map<String, Object> data = generateAllData();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String fileName = "menu_data_" + timestamp + ".json";
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "face_menu_publish");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        File jsonFile = new File(tempDir, fileName);
        
        try (FileWriter writer = new FileWriter(jsonFile, StandardCharsets.UTF_8)) {
            JSON.writeJSONString(writer, data, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
        }
        
        return jsonFile;
    }

    public File generateZipFile() throws IOException {
        Map<String, Object> allData = generateAllData();
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "face_menu_publish_" + timestamp);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        File fullDataFile = new File(tempDir, "full_data.json");
        try (FileWriter writer = new FileWriter(fullDataFile, StandardCharsets.UTF_8)) {
            JSON.writeJSONString(writer, allData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> slots = (Map<String, Object>) allData.get("slots");
        if (slots != null) {
            for (Map.Entry<String, Object> entry : slots.entrySet()) {
                String slotCode = entry.getKey();
                @SuppressWarnings("unchecked")
                Map<String, Object> slotData = (Map<String, Object>) entry.getValue();
                
                Map<String, Object> slotExport = new LinkedHashMap<>();
                slotExport.put("slotCode", slotCode);
                slotExport.put("slotName", slotData.get("slotName"));
                slotExport.put("menuTrees", slotData.get("menuTrees"));
                slotExport.put("pages", slotData.get("pages"));
                
                File slotFile = new File(tempDir, "slot_" + slotCode + ".json");
                try (FileWriter writer = new FileWriter(slotFile, StandardCharsets.UTF_8)) {
                    JSON.writeJSONString(writer, slotExport, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
                }
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allMenus = (List<Map<String, Object>>) allData.get("allMenus");
        if (allMenus != null) {
            File menusFile = new File(tempDir, "all_menus.json");
            try (FileWriter writer = new FileWriter(menusFile, StandardCharsets.UTF_8)) {
                JSON.writeJSONString(writer, allMenus, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
            }
        }

        File zipFile = new File(System.getProperty("java.io.tmpdir"), "face_menu_publish_" + timestamp + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zos.putNextEntry(zipEntry);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                }
            }
        }

        for (File file : Objects.requireNonNull(tempDir.listFiles())) {
            file.delete();
        }
        tempDir.delete();

        return zipFile;
    }
}
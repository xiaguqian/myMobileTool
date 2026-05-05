package com.facemenu.admin.service;

import com.facemenu.admin.entity.Menu;
import com.facemenu.admin.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class MenuService {

    @Autowired
    private MenuRepository menuRepository;

    public List<Menu> findAll() {
        return menuRepository.findAll();
    }

    public Optional<Menu> findById(Long id) {
        return menuRepository.findById(id);
    }

    public List<Menu> findByTreeId(Long treeId) {
        return menuRepository.findByTreeIdOrderBySortOrder(treeId);
    }

    public List<Menu> buildMenuTree(Long treeId) {
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

        buildChildren(rootMenus, parentMap);
        return rootMenus;
    }

    private void buildChildren(List<Menu> menus, Map<Long, List<Menu>> parentMap) {
        for (Menu menu : menus) {
            Long id = menu.getId();
            List<Menu> children = parentMap.get(id);
            if (children != null && !children.isEmpty()) {
                buildChildren(children, parentMap);
            }
        }
    }

    @Transactional
    public Menu save(Menu menu) {
        if (menu.getMenuCode() == null || menu.getMenuCode().isEmpty()) {
            menu.setMenuCode("MENU_" + System.currentTimeMillis());
        }
        return menuRepository.save(menu);
    }

    @Transactional
    public void deleteById(Long id) {
        deleteWithChildren(id);
    }

    private void deleteWithChildren(Long parentId) {
        List<Menu> children = menuRepository.findByParentIdOrderBySortOrder(parentId);
        for (Menu child : children) {
            deleteWithChildren(child.getId());
        }
        menuRepository.deleteById(parentId);
    }

    public List<Menu> findByTreeIds(List<Long> treeIds) {
        return menuRepository.findByTreeIdInOrderBySortOrder(treeIds);
    }
}
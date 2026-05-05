package com.facemenu.admin.service;

import com.facemenu.admin.entity.MenuTree;
import com.facemenu.admin.repository.MenuTreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MenuTreeService {

    @Autowired
    private MenuTreeRepository menuTreeRepository;

    public List<MenuTree> findAll() {
        return menuTreeRepository.findAll();
    }

    public Optional<MenuTree> findById(Long id) {
        return menuTreeRepository.findById(id);
    }

    public Optional<MenuTree> findByTreeCode(String treeCode) {
        return menuTreeRepository.findByTreeCode(treeCode);
    }

    @Transactional
    public MenuTree save(MenuTree menuTree) {
        if (menuTree.getId() == null) {
            if (menuTreeRepository.existsByTreeCode(menuTree.getTreeCode())) {
                throw new RuntimeException("菜单树编码已存在: " + menuTree.getTreeCode());
            }
        } else {
            Optional<MenuTree> existing = menuTreeRepository.findById(menuTree.getId());
            if (existing.isPresent() && !existing.get().getTreeCode().equals(menuTree.getTreeCode())) {
                if (menuTreeRepository.existsByTreeCode(menuTree.getTreeCode())) {
                    throw new RuntimeException("菜单树编码已存在: " + menuTree.getTreeCode());
                }
            }
        }
        return menuTreeRepository.save(menuTree);
    }

    @Transactional
    public void deleteById(Long id) {
        menuTreeRepository.deleteById(id);
    }
}
package com.facemenu.admin.service;

import com.facemenu.admin.entity.MenuTree;
import com.facemenu.admin.entity.SlotMenuTree;
import com.facemenu.admin.repository.MenuTreeRepository;
import com.facemenu.admin.repository.SlotMenuTreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SlotMenuTreeService {

    @Autowired
    private SlotMenuTreeRepository slotMenuTreeRepository;

    @Autowired
    private MenuTreeRepository menuTreeRepository;

    public List<SlotMenuTree> findBySlotId(Long slotId) {
        return slotMenuTreeRepository.findBySlotIdOrderBySortOrder(slotId);
    }

    public List<Map<String, Object>> findSlotMenuTreesWithDetails(Long slotId) {
        List<SlotMenuTree> slotMenuTrees = slotMenuTreeRepository.findBySlotIdOrderBySortOrder(slotId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (SlotMenuTree smt : slotMenuTrees) {
            Optional<MenuTree> menuTreeOpt = menuTreeRepository.findById(smt.getTreeId());
            if (menuTreeOpt.isPresent()) {
                MenuTree menuTree = menuTreeOpt.get();
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", smt.getId());
                item.put("slotId", smt.getSlotId());
                item.put("treeId", menuTree.getId());
                item.put("treeCode", menuTree.getTreeCode());
                item.put("treeName", menuTree.getTreeName());
                item.put("sortOrder", smt.getSortOrder());
                result.add(item);
            }
        }
        return result;
    }

    public List<Long> findTreeIdsBySlotId(Long slotId) {
        List<SlotMenuTree> slotMenuTrees = slotMenuTreeRepository.findBySlotIdOrderBySortOrder(slotId);
        List<Long> treeIds = new ArrayList<>();
        for (SlotMenuTree smt : slotMenuTrees) {
            treeIds.add(smt.getTreeId());
        }
        return treeIds;
    }

    @Transactional
    public SlotMenuTree save(SlotMenuTree slotMenuTree) {
        Optional<SlotMenuTree> existing = slotMenuTreeRepository.findBySlotIdAndTreeId(
                slotMenuTree.getSlotId(), slotMenuTree.getTreeId());
        
        if (existing.isPresent() && (slotMenuTree.getId() == null || !existing.get().getId().equals(slotMenuTree.getId()))) {
            throw new RuntimeException("该栏位已关联此菜单树");
        }
        
        return slotMenuTreeRepository.save(slotMenuTree);
    }

    @Transactional
    public void deleteById(Long id) {
        slotMenuTreeRepository.deleteById(id);
    }

    @Transactional
    public void updateSlotMenuTrees(Long slotId, List<SlotMenuTree> menuTrees) {
        slotMenuTreeRepository.deleteBySlotId(slotId);
        for (SlotMenuTree smt : menuTrees) {
            smt.setSlotId(slotId);
            smt.setId(null);
            slotMenuTreeRepository.save(smt);
        }
    }
}
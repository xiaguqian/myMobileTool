package com.facemenu.admin.repository;

import com.facemenu.admin.entity.SlotMenuTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlotMenuTreeRepository extends JpaRepository<SlotMenuTree, Long> {
    List<SlotMenuTree> findBySlotIdOrderBySortOrder(Long slotId);
    List<SlotMenuTree> findByTreeId(Long treeId);
    void deleteBySlotId(Long slotId);
    void deleteBySlotIdAndTreeId(Long slotId, Long treeId);
    Optional<SlotMenuTree> findBySlotIdAndTreeId(Long slotId, Long treeId);
}
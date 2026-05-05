package com.facemenu.user.repository;

import com.facemenu.user.entity.SlotMenuTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotMenuTreeRepository extends JpaRepository<SlotMenuTree, Long> {
    List<SlotMenuTree> findBySlotIdOrderBySortOrder(Long slotId);
}
package com.facemenu.admin.repository;

import com.facemenu.admin.entity.SlotPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlotPageRepository extends JpaRepository<SlotPage, Long> {
    List<SlotPage> findBySlotIdOrderBySortOrder(Long slotId);
    List<SlotPage> findByPageId(Long pageId);
    void deleteBySlotId(Long slotId);
    void deleteBySlotIdAndPageId(Long slotId, Long pageId);
    Optional<SlotPage> findBySlotIdAndPageId(Long slotId, Long pageId);
}
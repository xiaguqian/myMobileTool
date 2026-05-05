package com.facemenu.user.repository;

import com.facemenu.user.entity.SlotPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotPageRepository extends JpaRepository<SlotPage, Long> {
    List<SlotPage> findBySlotIdOrderBySortOrder(Long slotId);
}
package com.facemenu.admin.repository;

import com.facemenu.admin.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    Optional<Slot> findBySlotCode(String slotCode);
    boolean existsBySlotCode(String slotCode);
}
package com.facemenu.user.repository;

import com.facemenu.user.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    Optional<Slot> findBySlotCode(String slotCode);
    Optional<Slot> findBySlotCodeAndStatus(String slotCode, Integer status);
}
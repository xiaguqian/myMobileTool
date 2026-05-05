package com.facemenu.admin.service;

import com.facemenu.admin.entity.Slot;
import com.facemenu.admin.repository.SlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    public List<Slot> findAll() {
        return slotRepository.findAll();
    }

    public Optional<Slot> findById(Long id) {
        return slotRepository.findById(id);
    }

    public Optional<Slot> findBySlotCode(String slotCode) {
        return slotRepository.findBySlotCode(slotCode);
    }

    @Transactional
    public Slot save(Slot slot) {
        if (slot.getId() == null) {
            if (slotRepository.existsBySlotCode(slot.getSlotCode())) {
                throw new RuntimeException("栏位号已存在: " + slot.getSlotCode());
            }
        } else {
            Optional<Slot> existing = slotRepository.findById(slot.getId());
            if (existing.isPresent() && !existing.get().getSlotCode().equals(slot.getSlotCode())) {
                if (slotRepository.existsBySlotCode(slot.getSlotCode())) {
                    throw new RuntimeException("栏位号已存在: " + slot.getSlotCode());
                }
            }
        }
        return slotRepository.save(slot);
    }

    @Transactional
    public void deleteById(Long id) {
        slotRepository.deleteById(id);
    }
}
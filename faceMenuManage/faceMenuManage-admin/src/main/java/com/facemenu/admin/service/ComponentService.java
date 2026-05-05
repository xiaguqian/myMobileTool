package com.facemenu.admin.service;

import com.facemenu.admin.entity.Component;
import com.facemenu.admin.repository.ComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ComponentService {

    @Autowired
    private ComponentRepository componentRepository;

    public List<Component> findAll() {
        return componentRepository.findAll();
    }

    public Optional<Component> findById(Long id) {
        return componentRepository.findById(id);
    }

    public Optional<Component> findByComponentCode(String componentCode) {
        return componentRepository.findByComponentCode(componentCode);
    }

    @Transactional
    public Component save(Component component) {
        if (component.getId() == null) {
            if (componentRepository.existsByComponentCode(component.getComponentCode())) {
                throw new RuntimeException("组件编码已存在: " + component.getComponentCode());
            }
        } else {
            Optional<Component> existing = componentRepository.findById(component.getId());
            if (existing.isPresent() && !existing.get().getComponentCode().equals(component.getComponentCode())) {
                if (componentRepository.existsByComponentCode(component.getComponentCode())) {
                    throw new RuntimeException("组件编码已存在: " + component.getComponentCode());
                }
            }
        }
        return componentRepository.save(component);
    }

    @Transactional
    public void deleteById(Long id) {
        componentRepository.deleteById(id);
    }
}
package com.facemenu.admin.repository;

import com.facemenu.admin.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    Optional<Component> findByComponentCode(String componentCode);
    boolean existsByComponentCode(String componentCode);
}
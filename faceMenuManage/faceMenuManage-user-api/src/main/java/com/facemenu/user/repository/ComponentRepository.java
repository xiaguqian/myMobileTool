package com.facemenu.user.repository;

import com.facemenu.user.entity.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {
    Optional<Component> findByComponentCode(String componentCode);
    Optional<Component> findByIdAndStatus(Long id, Integer status);
}
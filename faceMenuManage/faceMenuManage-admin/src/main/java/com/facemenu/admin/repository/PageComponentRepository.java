package com.facemenu.admin.repository;

import com.facemenu.admin.entity.PageComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageComponentRepository extends JpaRepository<PageComponent, Long> {
    List<PageComponent> findByPageIdOrderBySortOrder(Long pageId);
    void deleteByPageId(Long pageId);
    void deleteByPageIdAndComponentId(Long pageId, Long componentId);
    Optional<PageComponent> findByPageIdAndComponentId(Long pageId, Long componentId);
}
package com.facemenu.user.repository;

import com.facemenu.user.entity.PageComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageComponentRepository extends JpaRepository<PageComponent, Long> {
    List<PageComponent> findByPageIdOrderBySortOrder(Long pageId);
}
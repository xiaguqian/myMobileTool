package com.facemenu.admin.repository;

import com.facemenu.admin.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    Optional<Page> findByPageCode(String pageCode);
    boolean existsByPageCode(String pageCode);
}
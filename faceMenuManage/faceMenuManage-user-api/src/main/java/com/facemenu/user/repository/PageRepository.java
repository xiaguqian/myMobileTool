package com.facemenu.user.repository;

import com.facemenu.user.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    Optional<Page> findByPageCode(String pageCode);
    Optional<Page> findByIdAndStatus(Long id, Integer status);
}
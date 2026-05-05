package com.facemenu.admin.repository;

import com.facemenu.admin.entity.MenuTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuTreeRepository extends JpaRepository<MenuTree, Long> {
    Optional<MenuTree> findByTreeCode(String treeCode);
    boolean existsByTreeCode(String treeCode);
}
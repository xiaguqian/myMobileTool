package com.facemenu.user.repository;

import com.facemenu.user.entity.MenuTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuTreeRepository extends JpaRepository<MenuTree, Long> {
    Optional<MenuTree> findByTreeCode(String treeCode);
    Optional<MenuTree> findByIdAndStatus(Long id, Integer status);
}
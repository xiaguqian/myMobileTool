package com.facemenu.admin.repository;

import com.facemenu.admin.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Optional<Menu> findByMenuCode(String menuCode);
    List<Menu> findByTreeIdOrderBySortOrder(Long treeId);
    List<Menu> findByParentIdOrderBySortOrder(Long parentId);
    List<Menu> findByTreeIdAndParentIdOrderBySortOrder(Long treeId, Long parentId);
    List<Menu> findByTreeIdInOrderBySortOrder(List<Long> treeIds);
    @Query("SELECT m FROM Menu m WHERE m.treeId = :treeId ORDER BY m.sortOrder, m.id")
    List<Menu> findAllByTreeIdOrdered(Long treeId);
}
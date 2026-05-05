package com.facemenu.user.repository;

import com.facemenu.user.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByTreeIdOrderBySortOrder(Long treeId);
    List<Menu> findByTreeIdAndStatusOrderBySortOrder(Long treeId, Integer status);
    List<Menu> findByTreeIdInAndStatusOrderBySortOrder(List<Long> treeIds, Integer status);
    List<Menu> findByStatusOrderBySortOrder(Integer status);
}
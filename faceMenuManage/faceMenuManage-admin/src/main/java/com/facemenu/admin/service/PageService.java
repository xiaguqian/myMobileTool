package com.facemenu.admin.service;

import com.facemenu.admin.entity.Page;
import com.facemenu.admin.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    public List<Page> findAll() {
        return pageRepository.findAll();
    }

    public Optional<Page> findById(Long id) {
        return pageRepository.findById(id);
    }

    public Optional<Page> findByPageCode(String pageCode) {
        return pageRepository.findByPageCode(pageCode);
    }

    @Transactional
    public Page save(Page page) {
        if (page.getId() == null) {
            if (pageRepository.existsByPageCode(page.getPageCode())) {
                throw new RuntimeException("页面编码已存在: " + page.getPageCode());
            }
        } else {
            Optional<Page> existing = pageRepository.findById(page.getId());
            if (existing.isPresent() && !existing.get().getPageCode().equals(page.getPageCode())) {
                if (pageRepository.existsByPageCode(page.getPageCode())) {
                    throw new RuntimeException("页面编码已存在: " + page.getPageCode());
                }
            }
        }
        return pageRepository.save(page);
    }

    @Transactional
    public void deleteById(Long id) {
        pageRepository.deleteById(id);
    }
}
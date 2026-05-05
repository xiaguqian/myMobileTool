package com.facemenu.admin.service;

import com.facemenu.admin.entity.Page;
import com.facemenu.admin.entity.SlotPage;
import com.facemenu.admin.repository.PageRepository;
import com.facemenu.admin.repository.SlotPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SlotPageService {

    @Autowired
    private SlotPageRepository slotPageRepository;

    @Autowired
    private PageRepository pageRepository;

    public List<SlotPage> findBySlotId(Long slotId) {
        return slotPageRepository.findBySlotIdOrderBySortOrder(slotId);
    }

    public List<Map<String, Object>> findSlotPagesWithDetails(Long slotId) {
        List<SlotPage> slotPages = slotPageRepository.findBySlotIdOrderBySortOrder(slotId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (SlotPage sp : slotPages) {
            Optional<Page> pageOpt = pageRepository.findById(sp.getPageId());
            if (pageOpt.isPresent()) {
                Page page = pageOpt.get();
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", sp.getId());
                item.put("slotId", sp.getSlotId());
                item.put("pageId", page.getId());
                item.put("pageCode", page.getPageCode());
                item.put("pageName", page.getPageName());
                item.put("routePath", page.getRoutePath());
                item.put("sortOrder", sp.getSortOrder());
                result.add(item);
            }
        }
        return result;
    }

    public List<Long> findPageIdsBySlotId(Long slotId) {
        List<SlotPage> slotPages = slotPageRepository.findBySlotIdOrderBySortOrder(slotId);
        List<Long> pageIds = new ArrayList<>();
        for (SlotPage sp : slotPages) {
            pageIds.add(sp.getPageId());
        }
        return pageIds;
    }

    @Transactional
    public SlotPage save(SlotPage slotPage) {
        Optional<SlotPage> existing = slotPageRepository.findBySlotIdAndPageId(
                slotPage.getSlotId(), slotPage.getPageId());
        
        if (existing.isPresent() && (slotPage.getId() == null || !existing.get().getId().equals(slotPage.getId()))) {
            throw new RuntimeException("该栏位已关联此页面");
        }
        
        return slotPageRepository.save(slotPage);
    }

    @Transactional
    public void deleteById(Long id) {
        slotPageRepository.deleteById(id);
    }

    @Transactional
    public void updateSlotPages(Long slotId, List<SlotPage> pages) {
        slotPageRepository.deleteBySlotId(slotId);
        for (SlotPage sp : pages) {
            sp.setSlotId(slotId);
            sp.setId(null);
            slotPageRepository.save(sp);
        }
    }
}
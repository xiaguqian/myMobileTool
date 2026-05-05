package com.facemenu.user.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "page_component")
public class PageComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "page_id", nullable = false)
    private Long pageId;

    @Column(name = "component_id", nullable = false)
    private Long componentId;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "config_override", columnDefinition = "LONGTEXT")
    private String configOverride;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}
package com.facemenu.admin.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "slot")
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_code", unique = true, nullable = false)
    private String slotCode;

    @Column(name = "slot_name")
    private String slotName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "positions_json", columnDefinition = "TEXT")
    private String positionsJson;

    @Column(name = "status")
    private Integer status = 1;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    private List<String> positions;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    public List<String> getPositions() {
        if (this.positionsJson == null || this.positionsJson.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            JSONArray array = JSON.parseArray(this.positionsJson);
            if (array == null) {
                return new ArrayList<>();
            }
            return array.toJavaList(String.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void setPositions(List<String> positions) {
        this.positions = positions;
        if (positions == null) {
            this.positionsJson = "[]";
        } else {
            this.positionsJson = JSON.toJSONString(positions);
        }
    }

    public void setPositionsJson(String positionsJson) {
        this.positionsJson = positionsJson;
        this.positions = null;
    }
}
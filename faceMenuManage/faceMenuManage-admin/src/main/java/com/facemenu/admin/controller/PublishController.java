package com.facemenu.admin.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.facemenu.admin.service.PublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequestMapping("/publish")
public class PublishController {

    @Autowired
    private PublishService publishService;

    @GetMapping("")
    public String publishPage(Model model) {
        return "publish/index";
    }

    @GetMapping("/preview")
    @ResponseBody
    public String preview() {
        Map<String, Object> data = publishService.generateAllData();
        return JSON.toJSONString(data, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
    }

    @GetMapping("/download/json")
    public ResponseEntity<Resource> downloadJson() throws IOException {
        File jsonFile = publishService.generateJsonFile();
        
        Resource resource = new FileSystemResource(jsonFile);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + 
                URLEncoder.encode(jsonFile.getName(), StandardCharsets.UTF_8.toString()));
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(jsonFile.length())
                .body(resource);
    }

    @GetMapping("/download/zip")
    public ResponseEntity<Resource> downloadZip() throws IOException {
        File zipFile = publishService.generateZipFile();
        
        Resource resource = new FileSystemResource(zipFile);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + 
                URLEncoder.encode(zipFile.getName(), StandardCharsets.UTF_8.toString()));
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip");
        
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFile.length())
                .body(resource);
    }
}
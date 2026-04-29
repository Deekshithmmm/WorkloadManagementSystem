package com.wms.controller;

import com.wms.entity.FileEntity;
import com.wms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@CrossOrigin("*")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public FileEntity upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam Long taskId,
            @RequestParam Long userId) {

        return fileService.uploadFile(file, taskId, userId);
    }

    @GetMapping("/task/{taskId}")
    public List<FileEntity> getFiles(@PathVariable Long taskId) {
        return fileService.getFilesByTask(taskId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws Exception {

        FileEntity file = fileService.getFile(id);
        Path path = Paths.get(file.getFilePath()).toAbsolutePath();

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(resource);
    }
}
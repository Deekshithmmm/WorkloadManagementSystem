package com.wms.service;

import org.springframework.web.multipart.MultipartFile;
import com.wms.entity.FileEntity;

import java.util.List;

public interface FileService {

    FileEntity uploadFile(MultipartFile file, Long taskId, Long userId);

    List<FileEntity> getFilesByTask(Long taskId);

    FileEntity getFile(Long id);
}

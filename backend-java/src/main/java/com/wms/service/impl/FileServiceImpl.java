package com.wms.service.impl;

import com.wms.entity.FileEntity;
import com.wms.repository.FileRepository;
import com.wms.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileRepository fileRepository;

    @Override
    public FileEntity uploadFile(MultipartFile file, Long taskId, Long userId) {
        try {
            // 🔥 Get absolute path of project (backend-java)
            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator;

            // 🔥 Create unique file name
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 🔥 Full file path
            String fullPath = uploadDir + fileName;

            // 🔥 Create file object
            File dest = new File(fullPath);

            // 🔥 Ensure directory exists
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            // 🔥 Save file
            file.transferTo(dest);

            // 🔥 Save to DB
            FileEntity entity = new FileEntity();
            entity.setFileName(fileName);
            entity.setFilePath(fullPath);   // store FULL path
            entity.setTaskId(taskId);
            entity.setUploadedBy(userId);

            return fileRepository.save(entity);

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 VERY IMPORTANT for debugging
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    @Override
    public List<FileEntity> getFilesByTask(Long taskId) {
        return fileRepository.findByTaskId(taskId);
    }

    @Override
    public FileEntity getFile(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
    }
}
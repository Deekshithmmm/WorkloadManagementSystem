// controller/TaskController.java
package com.wms.controller;

import com.wms.model.Task;
import com.wms.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    @Autowired private TaskRepository taskRepository;

    @GetMapping
    public List<Task> getAll() { return taskRepository.findAll(); }
}
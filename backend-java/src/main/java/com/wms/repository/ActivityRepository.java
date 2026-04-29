// repository/ActivityRepository.java
package com.wms.repository;

import com.wms.model.EmployeeActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<EmployeeActivity, Long> {}
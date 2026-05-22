package com.sbadss.repository;

import com.sbadss.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

     // Custom query: Spring will automatically write the SQL query for this!
    List<AuditLog> findByAction(String action);
    
    // Another custom query: Find logs performed by a specific user
    List<AuditLog> findByPerformedById(Long userId);
}

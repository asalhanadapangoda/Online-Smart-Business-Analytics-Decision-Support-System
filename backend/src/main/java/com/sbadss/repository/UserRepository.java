package com.sbadss.repository;

import com.sbadss.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByBranchId(Long branchId);

    /**
     * Returns the branch ID for a given username as a raw scalar value.
     * Completely bypasses JPA entity loading — no lazy/eager proxy involved.
     */
    @Query("SELECT u.branch.id FROM User u WHERE u.username = :username")
    Optional<Long> findBranchIdByUsername(@Param("username") String username);

    /**
     * Returns the role name for a given username as a raw scalar value.
     * Completely bypasses JPA entity loading — no lazy/eager proxy involved.
     */
    @Query("SELECT u.role.name FROM User u WHERE u.username = :username")
    Optional<String> findRoleNameByUsername(@Param("username") String username);
}

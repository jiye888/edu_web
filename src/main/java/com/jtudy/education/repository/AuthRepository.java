package com.jtudy.education.repository;

import com.jtudy.education.entity.Auth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    Page<Auth> findByIsProcessed(boolean isProcessed, Pageable pageable);

}

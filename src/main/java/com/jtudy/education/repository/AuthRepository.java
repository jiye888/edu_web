package com.jtudy.education.repository;

import com.jtudy.education.entity.Auth;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    @Query("select a from Auth a left outer join a.member m where m.email = :email")
    Auth findByEmail(String email);

    Auth findByAuthId(Long authId);

    Slice<Auth> findByIsProcessed(boolean isProcessed, Pageable pageable);

    @Query("select a from Auth where a.isProcessed = :isProcessed and a.processedDate = :date")
    List<Auth> findByIsProcessedAndProcessedDate(boolean isProcessed, LocalDateTime date);

}

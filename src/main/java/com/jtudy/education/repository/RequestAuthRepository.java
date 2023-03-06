package com.jtudy.education.repository;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.RequestAuth;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestAuthRepository extends CrudRepository<RequestAuth, String> {

}

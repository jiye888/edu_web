package com.jtudy.education.repository;

import com.jtudy.education.entity.RequestAuth;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestAuthRepository extends CrudRepository<RequestAuth, String> {

}

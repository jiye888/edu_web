package com.jtudy.education.service;

import com.jtudy.education.constant.Roles;
import com.jtudy.education.entity.RequestAuth;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface AuthService {

    void requestAuth(String email, Roles roles);

    RequestAuth getOne(String email, Roles roles);

    Slice<RequestAuth> requestedAuths(Pageable pageable);

    void acceptAuth(String email, Roles roles);

    void rejectAuth(String email, Roles roles);

    void deleteAuth(String email, Roles roles);

}

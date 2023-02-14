package com.jtudy.education.service;

import com.jtudy.education.constant.Roles;

public interface AuthService {

    void requestAuth(String email, Roles roles);

    void acceptAuth(String email, Roles roles);

    void rejectAuth(String email, Roles roles);

    void deleteAuth(String email, Roles roles);

}

package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.User;

public interface IRoleService {
    void assignRoles(User user);
}

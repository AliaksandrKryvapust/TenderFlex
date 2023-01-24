package com.exadel.tenderflex.service.api;

import com.exadel.tenderflex.repository.entity.User;

public interface IUserService extends IService<User>, IServiceUpdate<User>{
    User getUser(String email);
}

package com.ihrm.system.dao;

import com.ihrm.domain.system.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserResitory extends JpaRepository<User,String>,JpaSpecificationExecutor<User> {

  User findUserByMobile(String mobile);

}

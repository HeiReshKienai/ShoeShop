package com.hutech.ShoeShop.repository;

import com.hutech.ShoeShop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {

}

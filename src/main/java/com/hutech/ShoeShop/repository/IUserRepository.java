package com.hutech.ShoeShop.repository;

import com.hutech.ShoeShop.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.email = :email, u.fullName = :fullName, u.address = :address, u.phone = :phone WHERE u.username = :username")
    void updateUserByUsername(String username, String email, String fullName, String address, String phone);
}
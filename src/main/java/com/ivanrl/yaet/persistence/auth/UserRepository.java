package com.ivanrl.yaet.persistence.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserPO, Integer> {

    Optional<UserPO> findByEmail(String email);
}

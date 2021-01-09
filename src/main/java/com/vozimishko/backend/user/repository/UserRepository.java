package com.vozimishko.backend.user.repository;

import com.vozimishko.backend.user.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

  @Query("SELECT * FROM USERS WHERE email = :email")
  Optional<User> findByEmail(@Param("email") String email);

  @Query("SELECT * FROM USERS WHERE PHONE_NUMBER = :phoneNumber")
  Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}

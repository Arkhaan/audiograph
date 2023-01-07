package com.audiobank.demo.repositories;

import com.audiobank.demo.models.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email=:email and u.password=:password")
    Optional<User> findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    @Query("select u from User u where u.apikey=:apikey")
    Optional<User> findByApiKey(@Param("apikey") String apikey);

    @Query("select u from User u where u.email=NULL")
    List<User> findUnclaimedNames();

    @Query("select new java.lang.Boolean(count(*) > 0) from User u where u.email=:email")
    Boolean emailExists(@Param("email") String email);

    @Query("select new java.lang.Boolean(count(*) > 0) from User u where u.first_name=:first_name and u.last_name=:last_name and u.email <> NULL")
    Boolean isRegistered(@Param("first_name") String first_name, @Param("last_name") String last_name);
}
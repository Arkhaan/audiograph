package com.audiobank.demo.repositories;

import com.audiobank.demo.models.Tag;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("select t from Tag t where t.value=:value")
    Optional<Tag> getByValue(@Param("value") String value);
}
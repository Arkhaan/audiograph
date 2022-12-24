package com.audiobank.demo.repositories;

import com.audiobank.demo.models.Audiofile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AudiofileRepository extends JpaRepository<Audiofile, Long> {
}